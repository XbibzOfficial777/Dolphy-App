package com.droid.dolphy.tvcast

enum class CastProtocol { GOOGLE_CAST, DLNA_UPNP }

data class CastDevice(
    val id: String,
    val name: String,
    val protocol: CastProtocol,
    val endpoint: String,
    val controlUrl: String? = null,
)

enum class CastStatus {
    IDLE,
    CONNECTING,
    CONNECTED,
    LOADING,
    PLAYING,
    PAUSED,
    ERROR,
}

data class CastConnectionState(
    val status: CastStatus = CastStatus.IDLE,
    val device: CastDevice? = null,
    val detail: String? = null,
)

data class CastMedia(
    val title: String,
    val contentUrl: String,
    val mimeType: String,
)
