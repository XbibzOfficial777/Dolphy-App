package com.droid.dolphy.tvcast

import android.content.Context
import android.net.wifi.WifiManager
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.net.SocketTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal object SsdpDiscovery {
    private const val GROUP = "239.255.255.250"
    private const val PORT = 1900

    private fun request(searchTarget: String): ByteArray {
        return (
            "M-SEARCH * HTTP/1.1\r\n" +
                "HOST: $GROUP:$PORT\r\n" +
                "MAN: \"ssdp:discover\"\r\n" +
                "MX: 2\r\n" +
                "ST: $searchTarget\r\n\r\n"
            ).toByteArray(Charsets.UTF_8)
    }

    fun discover(timeoutMs: Long = 4000): List<SsdpAdvertisement> {
        val group = InetAddress.getByName(GROUP)
        val socket = MulticastSocket()
        return try {
            socket.soTimeout = 400
            socket.timeToLive = 2
            listOf(
                "urn:schemas-upnp-org:device:MediaRenderer:1",
                "urn:schemas-upnp-org:service:AVTransport:1",
            ).forEach { target ->
                val bytes = request(target)
                socket.send(DatagramPacket(bytes, bytes.size, group, PORT))
            }

            val deadline = System.nanoTime() + timeoutMs * 1_000_000
            val results = linkedMapOf<String, SsdpAdvertisement>()
            val buffer = ByteArray(8192)
            while (System.nanoTime() < deadline && results.size < 32) {
                val packet = DatagramPacket(buffer, buffer.size)
                try {
                    socket.receive(packet)
                    val text = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    val advertisement = parseSsdpAdvertisement(text, packet.address.hostAddress ?: "") ?: continue
                    results.putIfAbsent(advertisement.location, advertisement)
                } catch (_: SocketTimeoutException) {
                    // Discovery is time-bounded; timeouts are normal between replies.
                }
            }
            results.values.toList()
        } finally {
            socket.close()
        }
    }
}

class DlnaController(
    context: Context,
    private val onDevices: (List<CastDevice>) -> Unit,
    private val onScanning: (Boolean) -> Unit,
    private val onDiscoveryError: (String?) -> Unit,
    private val onState: (CastConnectionState) -> Unit,
) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val client = DlnaAvTransportClient
    private var discoveryJob: Job? = null
    private var multicastLock: WifiManager.MulticastLock? = null
    private var currentDevice: CastDevice? = null
    private var currentState = CastConnectionState()

    private fun emit(state: CastConnectionState) {
        currentState = state
        onState(state)
    }

    private fun acquireMulticastLock() {
        if (multicastLock?.isHeld == true) return
        val wifi = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifi.createMulticastLock("dolphy-dlna-discovery").apply {
            setReferenceCounted(false)
            acquire()
        }
    }

    private fun releaseMulticastLock() {
        multicastLock?.let { if (it.isHeld) it.release() }
        multicastLock = null
    }

    fun startDiscovery() {
        if (discoveryJob?.isActive == true) return
        discoveryJob = scope.launch {
            onScanning(true)
            onDiscoveryError(null)
            try {
                acquireMulticastLock()
                val advertisements = withContext(Dispatchers.IO) { SsdpDiscovery.discover() }
                val devices = withContext(Dispatchers.IO) {
                    coroutineScope {
                        advertisements.take(24).map { advertisement ->
                            async {
                                runCatching { fetchDlnaDevice(advertisement.location) }.getOrNull()
                            }
                        }.awaitAll().filterNotNull().distinctBy { it.id }.sortedBy { it.name.lowercase() }
                    }
                }
                onDevices(devices)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                onDevices(emptyList())
                onDiscoveryError("Penemuan DLNA gagal: ${e.message ?: e.javaClass.simpleName}")
            } finally {
                releaseMulticastLock()
                onScanning(false)
            }
        }
    }

    fun connect(device: CastDevice) {
        val controlUrl = device.controlUrl
        if (controlUrl == null) {
            emit(CastConnectionState(CastStatus.ERROR, device, "Renderer tidak memiliki AVTransport"))
            return
        }
        currentDevice = device
        emit(CastConnectionState(CastStatus.CONNECTING, device))
        scope.launch {
            try {
                val transportState = withContext(Dispatchers.IO) { client.getTransportState(controlUrl) }
                emit(
                    CastConnectionState(
                        CastStatus.CONNECTED,
                        device,
                        "AVTransport merespons — $transportState",
                    ),
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(CastConnectionState(CastStatus.ERROR, device, e.message ?: "Renderer tidak merespons"))
            }
        }
    }

    fun play(media: CastMedia) {
        val device = currentDevice
        val controlUrl = device?.controlUrl
        if (device == null || controlUrl == null || currentState.status == CastStatus.IDLE) {
            emit(CastConnectionState(CastStatus.ERROR, device, "Renderer DLNA belum diverifikasi"))
            return
        }
        emit(CastConnectionState(CastStatus.LOADING, device))
        scope.launch {
            try {
                withContext(Dispatchers.IO) { client.setMediaAndPlay(controlUrl, media) }
                delay(600)
                val transportState = withContext(Dispatchers.IO) { client.getTransportState(controlUrl) }
                if (transportState.equals("PLAYING", true) || transportState.equals("TRANSITIONING", true)) {
                    emit(CastConnectionState(CastStatus.PLAYING, device, transportState))
                } else {
                    emit(
                        CastConnectionState(
                            CastStatus.CONNECTED,
                            device,
                            "Perintah Play diterima; status renderer: $transportState",
                        ),
                    )
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(CastConnectionState(CastStatus.ERROR, device, e.message ?: "Playback DLNA gagal"))
            }
        }
    }

    fun togglePlayPause() {
        val device = currentDevice ?: return
        val controlUrl = device.controlUrl ?: return
        val pause = currentState.status == CastStatus.PLAYING
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (pause) client.pause(controlUrl) else client.play(controlUrl)
                }
                delay(300)
                val transportState = withContext(Dispatchers.IO) { client.getTransportState(controlUrl) }
                val status = when {
                    transportState.equals("PLAYING", true) -> CastStatus.PLAYING
                    transportState.startsWith("PAUSED", ignoreCase = true) -> CastStatus.PAUSED
                    else -> CastStatus.CONNECTED
                }
                emit(CastConnectionState(status, device, "Status renderer: $transportState"))
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(CastConnectionState(CastStatus.ERROR, device, e.message ?: "Perintah playback gagal"))
            }
        }
    }

    fun stopPlayback() {
        val device = currentDevice ?: return
        val controlUrl = device.controlUrl ?: return
        scope.launch {
            try {
                withContext(Dispatchers.IO) { client.stop(controlUrl) }
                emit(CastConnectionState(CastStatus.CONNECTED, device, "Renderer menerima Stop"))
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(CastConnectionState(CastStatus.ERROR, device, e.message ?: "Perintah Stop gagal"))
            }
        }
    }

    fun disconnect() {
        currentDevice = null
        emit(CastConnectionState())
    }

    fun close() {
        discoveryJob?.cancel()
        releaseMulticastLock()
        scope.cancel()
    }
}
