package com.droid.dolphy.tvcast

import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

internal data class SsdpAdvertisement(
    val location: String,
    val usn: String,
    val searchTarget: String,
    val address: String,
)

internal fun parseSsdpAdvertisement(text: String, address: String): SsdpAdvertisement? {
    val lines = text.replace("\r\n", "\n").split('\n')
    if (!lines.firstOrNull().orEmpty().startsWith("HTTP/1.1 200")) return null
    val headers = mutableMapOf<String, String>()
    lines.drop(1).forEach { line ->
        val separator = line.indexOf(':')
        if (separator > 0) {
            headers[line.substring(0, separator).trim().lowercase()] = line.substring(separator + 1).trim()
        }
    }
    val location = headers["location"].orEmpty()
    if (location.isBlank()) return null
    return SsdpAdvertisement(
        location = location,
        usn = headers["usn"].orEmpty(),
        searchTarget = headers["st"].orEmpty(),
        address = address,
    )
}

internal fun resolveDlnaControlUrl(location: String, controlUrl: String): String? {
    if (controlUrl.isBlank()) return null
    val resolved = runCatching { URI(location).resolve(controlUrl) }.getOrNull() ?: return null
    val scheme = resolved.scheme?.lowercase()
    if (scheme != "http" && scheme != "https") return null
    if (resolved.host.isNullOrBlank()) return null
    return resolved.toString()
}

private fun parseXml(xml: String) = DocumentBuilderFactory.newInstance().apply {
    setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    setFeature("http://xml.org/sax/features/external-general-entities", false)
    setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    isXIncludeAware = false
    isExpandEntityReferences = false
}.newDocumentBuilder().parse(xml.byteInputStream(Charsets.UTF_8))

private fun childText(parent: Element, tagName: String): String {
    val children = parent.childNodes
    for (index in 0 until children.length) {
        val child = children.item(index) as? Element ?: continue
        if (child.tagName == tagName) return child.textContent.trim()
    }
    return ""
}

internal fun parseDlnaDeviceDescription(xml: String, location: String): CastDevice? {
    val document = runCatching { parseXml(xml) }.getOrNull() ?: return null
    val services = document.getElementsByTagName("service")
    for (serviceIndex in 0 until services.length) {
        val service = services.item(serviceIndex) as? Element ?: continue
        if (!childText(service, "serviceType").contains("AVTransport", ignoreCase = true)) continue
        val controlUrl = resolveDlnaControlUrl(location, childText(service, "controlURL")) ?: continue
        val uri = runCatching { URI(controlUrl) }.getOrNull() ?: continue
        var deviceElement = service.parentNode as? Element
        while (deviceElement != null && deviceElement.tagName != "device") {
            deviceElement = deviceElement.parentNode as? Element
        }
        val device = deviceElement ?: continue
        val friendlyName = childText(device, "friendlyName").ifBlank { "DLNA renderer" }
        val udn = childText(device, "UDN").ifBlank { location }
        return CastDevice(
            id = udn,
            name = friendlyName,
            protocol = CastProtocol.DLNA_UPNP,
            endpoint = "${uri.host}:${if (uri.port >= 0) uri.port else 80}",
            controlUrl = controlUrl,
        )
    }
    return null
}

internal fun readNetworkText(url: String, maxBytes: Int, timeoutMs: Int = 4000): String {
    val connection = URL(url).openConnection() as HttpURLConnection
    return try {
        connection.connectTimeout = timeoutMs
        connection.readTimeout = timeoutMs
        connection.setRequestProperty("User-Agent", "Dolphy/2.8 UPnP")
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val bytes = stream?.use { input ->
            val buffer = ByteArray(maxBytes + 1)
            var offset = 0
            while (offset < buffer.size) {
                val read = input.read(buffer, offset, buffer.size - offset)
                if (read < 0) break
                offset += read
            }
            if (offset > maxBytes) throw IllegalArgumentException("Respons jaringan terlalu besar")
            buffer.copyOf(offset)
        } ?: ByteArray(0)
        if (connection.responseCode !in 200..299) {
            throw IllegalStateException("HTTP ${connection.responseCode}")
        }
        String(bytes, Charsets.UTF_8)
    } finally {
        connection.disconnect()
    }
}

internal fun fetchDlnaDevice(location: String): CastDevice? {
    val xml = readNetworkText(location, maxBytes = 512 * 1024)
    return parseDlnaDeviceDescription(xml, location)
}

internal fun xmlEscape(value: String): String = buildString(value.length) {
    value.forEach { character ->
        when (character) {
            '&' -> append("&amp;")
            '<' -> append("&lt;")
            '>' -> append("&gt;")
            '"' -> append("&quot;")
            '\'' -> append("&apos;")
            else -> append(character)
        }
    }
}

private data class SoapResponse(val code: Int, val body: String)

object DlnaAvTransportClient {
    private const val SERVICE = "urn:schemas-upnp-org:service:AVTransport:1"

    private fun soap(controlUrl: String, action: String, arguments: String): SoapResponse {
        val connection = URL(controlUrl).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 7000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"")
            connection.setRequestProperty("SOAPACTION", "\"$SERVICE#$action\"")
            val envelope = """<?xml version="1.0" encoding="utf-8"?>
                |<s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                |<s:Body><u:$action xmlns:u="$SERVICE">$arguments</u:$action></s:Body>
                |</s:Envelope>""".trimMargin()
            connection.outputStream.use { it.write(envelope.toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            SoapResponse(code, body)
        } finally {
            connection.disconnect()
        }
    }

    private fun requireSuccess(response: SoapResponse, action: String) {
        if (response.code !in 200..299) {
            val detail = response.body.take(240).ifBlank { "tanpa respons" }
            throw IllegalStateException("$action gagal: HTTP ${response.code} — $detail")
        }
    }

    fun getTransportState(controlUrl: String): String {
        val response = soap(
            controlUrl,
            "GetTransportInfo",
            "<InstanceID>0</InstanceID>",
        )
        requireSuccess(response, "GetTransportInfo")
        val document = parseXml(response.body)
        return document.getElementsByTagName("CurrentTransportState").item(0)?.textContent
            ?.takeIf { it.isNotBlank() } ?: "UNKNOWN"
    }

    fun setMediaAndPlay(controlUrl: String, media: CastMedia) {
        val upnpClass = when {
            media.mimeType.startsWith("audio/") -> "object.item.audioItem.musicTrack"
            media.mimeType.startsWith("image/") -> "object.item.imageItem.photo"
            else -> "object.item.videoItem"
        }
        val didl = """
            |<DIDL-Lite xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">
            |<item id="1" parentID="0" restricted="1"><dc:title>${xmlEscape(media.title)}</dc:title>
            |<upnp:class>$upnpClass</upnp:class>
            |<res protocolInfo="http-get:*:${xmlEscape(media.mimeType)}:*">${xmlEscape(media.contentUrl)}</res></item></DIDL-Lite>
        """.trimMargin()
        val setUri = soap(
            controlUrl,
            "SetAVTransportURI",
            "<InstanceID>0</InstanceID><CurrentURI>${xmlEscape(media.contentUrl)}</CurrentURI>" +
                "<CurrentURIMetaData>${xmlEscape(didl)}</CurrentURIMetaData>",
        )
        requireSuccess(setUri, "SetAVTransportURI")
        play(controlUrl)
    }

    fun play(controlUrl: String) {
        requireSuccess(
            soap(controlUrl, "Play", "<InstanceID>0</InstanceID><Speed>1</Speed>"),
            "Play",
        )
    }

    fun pause(controlUrl: String) {
        requireSuccess(soap(controlUrl, "Pause", "<InstanceID>0</InstanceID>"), "Pause")
    }

    fun stop(controlUrl: String) {
        requireSuccess(soap(controlUrl, "Stop", "<InstanceID>0</InstanceID>"), "Stop")
    }
}
