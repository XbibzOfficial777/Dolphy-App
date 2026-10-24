package com.droid.dolphy.tvcast

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/** Real Google Cast session and playback controller. Connection state only changes from SDK callbacks. */
class GoogleCastController(
    context: Context,
    private val onDevices: (List<CastDevice>) -> Unit,
    private val onState: (CastConnectionState) -> Unit,
) {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var castContext: CastContext? = null
    private var mediaRouter: MediaRouter? = null
    private var routeSelector: MediaRouteSelector? = null
    private var selectedRouteId: String? = null
    private var currentDevice: CastDevice? = null
    private var started = false

    private fun postDevices(devices: List<CastDevice>) {
        mainHandler.post { onDevices(devices) }
    }

    private fun postState(
        status: CastStatus,
        device: CastDevice? = currentDevice,
        detail: String? = null,
    ) {
        mainHandler.post { onState(CastConnectionState(status, device, detail)) }
    }

    private fun routeDevice(route: MediaRouter.RouteInfo): CastDevice {
        return CastDevice(
            id = route.id,
            name = route.name,
            protocol = CastProtocol.GOOGLE_CAST,
            endpoint = "Google Cast",
        )
    }

    private val routerCallback = object : MediaRouter.Callback() {
        override fun onRouteAdded(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshRoutes()
        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshRoutes()
        override fun onRouteRemoved(router: MediaRouter, route: MediaRouter.RouteInfo) = refreshRoutes()
    }

    private val mediaCallback = object : RemoteMediaClient.Callback() {
        override fun onStatusUpdated() {
            val client = castContext?.sessionManager?.currentCastSession?.remoteMediaClient ?: return
            when {
                client.isPlaying -> postState(CastStatus.PLAYING)
                client.isPaused -> postState(CastStatus.PAUSED)
                client.isBuffering -> postState(CastStatus.LOADING)
            }
        }
    }

    private val sessionListener = object : SessionManagerListener<CastSession> {
        override fun onSessionStarting(session: CastSession) {
            postState(CastStatus.CONNECTING)
        }

        override fun onSessionStarted(session: CastSession, sessionId: String) {
            session.remoteMediaClient?.registerCallback(mediaCallback)
            postState(CastStatus.CONNECTED)
        }

        override fun onSessionStartFailed(session: CastSession, error: Int) {
            postState(CastStatus.ERROR, detail = "Google Cast gagal tersambung (kode $error)")
        }

        override fun onSessionEnding(session: CastSession) {
            postState(CastStatus.CONNECTING, detail = "Memutuskan sesi")
        }

        override fun onSessionEnded(session: CastSession, error: Int) {
            session.remoteMediaClient?.unregisterCallback(mediaCallback)
            currentDevice = null
            selectedRouteId = null
            postState(CastStatus.IDLE, device = null)
        }

        override fun onSessionResuming(session: CastSession, sessionId: String) {
            postState(CastStatus.CONNECTING)
        }

        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
            session.remoteMediaClient?.registerCallback(mediaCallback)
            postState(CastStatus.CONNECTED)
        }

        override fun onSessionResumeFailed(session: CastSession, error: Int) {
            postState(CastStatus.ERROR, detail = "Sesi Google Cast tidak dapat dilanjutkan (kode $error)")
        }

        override fun onSessionSuspended(session: CastSession, reason: Int) {
            postState(CastStatus.IDLE, device = null, detail = "Sesi dihentikan sementara ($reason)")
        }
    }

    fun start() {
        if (started) return
        started = true
        val availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
        if (availability != ConnectionResult.SUCCESS) {
            started = false
            postDevices(emptyList())
            postState(
                CastStatus.ERROR,
                device = null,
                detail = "Layanan Google Play diperlukan untuk Google Cast (kode $availability)",
            )
            return
        }

        try {
            val cast = CastContext.getSharedInstance(appContext)
            val router = MediaRouter.getInstance(appContext)
            val selector = MediaRouteSelector.Builder()
                .addControlCategory(
                    CastMediaControlIntent.categoryForCast(
                        CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID,
                    ),
                )
                .build()
            castContext = cast
            mediaRouter = router
            routeSelector = selector
            cast.sessionManager.addSessionManagerListener(sessionListener, CastSession::class.java)
            router.addCallback(selector, routerCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
            refreshRoutes()

            val session = cast.sessionManager.currentCastSession
            if (session?.isConnected == true) {
                currentDevice = currentDevice ?: CastDevice(
                    id = selectedRouteId ?: session.castDevice?.deviceId ?: "google-cast",
                    name = session.castDevice?.friendlyName ?: "Google Cast",
                    protocol = CastProtocol.GOOGLE_CAST,
                    endpoint = "Google Cast",
                )
                session.remoteMediaClient?.registerCallback(mediaCallback)
                postState(CastStatus.CONNECTED)
            }
        } catch (e: Exception) {
            started = false
            postState(CastStatus.ERROR, device = null, detail = "Google Cast tidak tersedia: ${e.message}")
        }
    }

    private fun refreshRoutes() {
        val router = mediaRouter ?: return
        val selector = routeSelector ?: return
        postDevices(
            router.routes
                .filter { !it.isDefault && it.matchesSelector(selector) }
                .map(::routeDevice)
                .distinctBy { it.id }
                .sortedBy { it.name.lowercase() },
        )
    }

    fun connect(device: CastDevice) {
        val router = mediaRouter
        val route = router?.routes?.firstOrNull { it.id == device.id }
        if (route == null) {
            postState(CastStatus.ERROR, device = device, detail = "Perangkat Cast tidak lagi tersedia")
            return
        }
        currentDevice = device
        selectedRouteId = device.id
        postState(CastStatus.CONNECTING, device = device)
        route.select()
    }

    fun play(media: CastMedia) {
        val session = castContext?.sessionManager?.currentCastSession
        val client = session?.remoteMediaClient
        if (session?.isConnected != true || client == null) {
            postState(CastStatus.ERROR, detail = "Sesi Google Cast belum tersambung")
            return
        }

        val metadata = MediaMetadata(
            when {
                media.mimeType.startsWith("image/") -> MediaMetadata.MEDIA_TYPE_PHOTO
                media.mimeType.startsWith("audio/") -> MediaMetadata.MEDIA_TYPE_MUSIC_TRACK
                else -> MediaMetadata.MEDIA_TYPE_MOVIE
            },
        ).apply {
            putString(MediaMetadata.KEY_TITLE, media.title)
        }
        val mediaInfo = MediaInfo.Builder(media.contentUrl)
            .setContentType(media.mimeType)
            .setStreamType(
                if (media.mimeType.startsWith("image/")) MediaInfo.STREAM_TYPE_NONE
                else MediaInfo.STREAM_TYPE_BUFFERED,
            )
            .setMetadata(metadata)
            .build()
        val request = MediaLoadRequestData.Builder()
            .setMediaInfo(mediaInfo)
            .setAutoplay(true)
            .build()

        postState(CastStatus.LOADING)
        client.load(request).setResultCallback { result ->
            if (result.status.isSuccess) {
                postState(CastStatus.CONNECTED, detail = "Perintah memuat media diterima")
            } else {
                postState(CastStatus.ERROR, detail = "Media ditolak oleh receiver (${result.status})")
            }
        }
    }

    fun togglePlayPause() {
        val client = castContext?.sessionManager?.currentCastSession?.remoteMediaClient ?: return
        val pending = if (client.isPlaying) client.pause() else client.play()
        pending.setResultCallback { result ->
            if (!result.status.isSuccess) {
                postState(CastStatus.ERROR, detail = "Perintah playback gagal (${result.status})")
            }
        }
    }

    fun stopPlayback() {
        val client = castContext?.sessionManager?.currentCastSession?.remoteMediaClient ?: return
        client.stop().setResultCallback { result ->
            if (result.status.isSuccess) {
                postState(CastStatus.CONNECTED, detail = "Playback dihentikan")
            } else {
                postState(CastStatus.ERROR, detail = "Tidak dapat menghentikan playback (${result.status})")
            }
        }
    }

    fun disconnect() {
        castContext?.sessionManager?.endCurrentSession(true)
    }

    fun close() {
        if (!started) return
        castContext?.sessionManager?.currentCastSession?.remoteMediaClient?.unregisterCallback(mediaCallback)
        castContext?.sessionManager?.removeSessionManagerListener(sessionListener, CastSession::class.java)
        mediaRouter?.removeCallback(routerCallback)
        started = false
    }
}
