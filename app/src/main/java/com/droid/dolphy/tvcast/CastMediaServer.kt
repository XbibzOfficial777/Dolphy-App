package com.droid.dolphy.tvcast

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.UUID

internal fun parseByteRange(header: String?, fileLength: Long): LongRange? {
    if (header.isNullOrBlank()) return null
    require(fileLength > 0) { "Empty media cannot be ranged" }
    val value = header.trim()
    require(value.startsWith("bytes=", ignoreCase = true)) { "Unsupported range unit" }
    val spec = value.substringAfter('=').trim()
    require(!spec.contains(',')) { "Multiple ranges are not supported" }
    val parts = spec.split('-', limit = 2)
    require(parts.size == 2) { "Malformed range" }
    val startText = parts[0].trim()
    val endText = parts[1].trim()

    return if (startText.isEmpty()) {
        val suffix = endText.toLongOrNull() ?: throw IllegalArgumentException("Malformed suffix range")
        require(suffix > 0) { "Invalid suffix range" }
        maxOf(0L, fileLength - suffix)..(fileLength - 1)
    } else {
        val start = startText.toLongOrNull() ?: throw IllegalArgumentException("Malformed range start")
        require(start >= 0 && start < fileLength) { "Range start outside media" }
        val requestedEnd = if (endText.isEmpty()) fileLength - 1
        else endText.toLongOrNull() ?: throw IllegalArgumentException("Malformed range end")
        require(requestedEnd >= start) { "Range end before start" }
        start..minOf(requestedEnd, fileLength - 1)
    }
}

private class DolphyMediaHttpServer(
    private val mediaFile: File,
    private val mimeType: String,
    port: Int,
) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        if (session.uri != "/media" && session.uri != "/play") {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found")
        }
        val length = mediaFile.length()
        val range = try {
            parseByteRange(session.headers["range"], length)
        } catch (_: IllegalArgumentException) {
            return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, "text/plain", "Invalid range")
                .apply { addHeader("Content-Range", "bytes */$length") }
        }
        val start = range?.first ?: 0L
        val end = range?.last ?: (length - 1)
        val contentLength = if (length == 0L) 0L else end - start + 1
        val status = if (range == null) Response.Status.OK else Response.Status.PARTIAL_CONTENT

        val response = if (session.method == Method.HEAD || contentLength == 0L) {
            newFixedLengthResponse(status, mimeType, null, contentLength)
        } else {
            val input = FileInputStream(mediaFile).apply {
                var remaining = start
                while (remaining > 0) {
                    val skipped = skip(remaining)
                    if (skipped <= 0) break
                    remaining -= skipped
                }
            }
            newFixedLengthResponse(status, mimeType, input, contentLength)
        }
        response.addHeader("Accept-Ranges", "bytes")
        response.addHeader("Content-Length", contentLength.toString())
        response.addHeader("Access-Control-Allow-Origin", "*")
        if (range != null) response.addHeader("Content-Range", "bytes $start-$end/$length")
        return response
    }
}

class ServedCastMedia internal constructor(
    val title: String,
    val file: File,
    val mimeType: String,
    val contentUrl: String,
    internal val server: NanoHTTPD,
) {
    fun asCastMedia() = CastMedia(title, contentUrl, mimeType)
}

object CastMediaServerManager {
    @Volatile
    var current: ServedCastMedia? = null
        private set

    @Synchronized
    fun serve(context: Context, uri: Uri): ServedCastMedia {
        val resolver = context.contentResolver
        var displayName = "Dolphy media"
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && !cursor.isNull(index)) displayName = cursor.getString(index)
            }
        }
        val safeTitle = displayName.substringAfterLast('/').substringAfterLast('\\').ifBlank { "Dolphy media" }
        val extension = safeTitle.substringAfterLast('.', "")
            .lowercase()
            .filter { it.isLetterOrDigit() }
            .take(10)
        var mimeType = resolver.getType(uri).orEmpty()
        if (mimeType.isBlank() || mimeType == "application/octet-stream") {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).orEmpty()
        }
        require(
            mimeType.startsWith("video/") || mimeType.startsWith("audio/") || mimeType.startsWith("image/"),
        ) { "Jenis media tidak didukung" }

        val directory = File(context.cacheDir, "cast-media").apply { mkdirs() }
        val suffix = if (extension.isBlank()) ".media" else ".$extension"
        val target = File(directory, UUID.randomUUID().toString() + suffix)
        var pendingServer: NanoHTTPD? = null
        try {
            resolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output, bufferSize = 128 * 1024) }
            } ?: throw IOException("File media tidak dapat dibuka")
            require(target.length() > 0) { "File media kosong" }

            val (server, port) = startServer(target, mimeType)
            pendingServer = server
            val ip = findLanIpv4()
            if (ip == null) {
                server.stop()
                throw IOException("Alamat IPv4 Wi-Fi/LAN tidak ditemukan")
            }
            val served = ServedCastMedia(
                title = safeTitle,
                file = target,
                mimeType = mimeType,
                contentUrl = "http://$ip:$port/media",
                server = server,
            )
            clear()
            current = served
            pendingServer = null
            return served
        } catch (e: Exception) {
            runCatching { pendingServer?.stop() }
            target.delete()
            throw e
        }
    }

    private fun startServer(file: File, mimeType: String): Pair<NanoHTTPD, Int> {
        for (port in 8080..8090) {
            val server = DolphyMediaHttpServer(file, mimeType, port)
            try {
                server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
                return server to port
            } catch (_: IOException) {
                server.stop()
            }
        }
        throw IOException("Port server media 8080–8090 tidak tersedia")
    }

    private fun findLanIpv4(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
        val candidates = interfaces.filter { it.isUp && !it.isLoopback }.flatMap { network ->
            network.inetAddresses.toList().filterIsInstance<Inet4Address>()
                .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress && !it.isAnyLocalAddress }
                .map { address -> Triple(network.name, address, address.isSiteLocalAddress) }
        }
        return candidates.sortedWith(
            compareByDescending<Triple<String, Inet4Address, Boolean>> { it.third }
                .thenByDescending { it.first.startsWith("wlan") || it.first.startsWith("wifi") }
                .thenBy { it.first },
        ).firstOrNull()?.second?.hostAddress
    }

    @Synchronized
    fun clear() {
        current?.let { served ->
            runCatching { served.server.stop() }
            served.file.delete()
        }
        current = null
    }
}
