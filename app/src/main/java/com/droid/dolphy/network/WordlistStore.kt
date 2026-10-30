package com.droid.dolphy.network

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.Writer
import java.util.Locale
import java.util.UUID

private const val MAX_WORDLIST_BYTES = 256L * 1024L * 1024L
private const val MAX_WORDLIST_ENTRIES = 20_000_000

internal data class ImportedWordlist(
    val suggestedName: String,
    val fileName: String,
    val count: Int,
    val validCount: Int,
)

internal data class NormalizedWordlistStats(val count: Int, val validCount: Int)

internal fun copyNormalizedWordlist(reader: BufferedReader, writer: Writer): NormalizedWordlistStats {
    var count = 0
    var validCount = 0
    while (true) {
        val line = reader.readLine() ?: break
        val password = normalizeWordlistLine(line) ?: continue
        if (count >= MAX_WORDLIST_ENTRIES) throw IOException("Wordlist melebihi 20 juta entri")
        writer.append(password).append('\n')
        count++
        if (isSupportedWpaPassphrase(password)) validCount++
    }
    return NormalizedWordlistStats(count, validCount)
}

internal fun wordlistBaseName(displayName: String): String {
    val fileName = displayName.substringAfterLast('/').substringAfterLast('\\').trim()
    return fileName.substringBeforeLast('.', fileName)
        .trim()
        .take(80)
        .ifBlank { "Wordlist" }
}

internal fun uniqueWordlistName(baseName: String, existingNames: Collection<String>): String {
    val occupied = existingNames.map { it.lowercase(Locale.ROOT) }.toSet()
    val base = baseName.ifBlank { "Wordlist" }
    if (base.lowercase(Locale.ROOT) !in occupied) return base
    var suffix = 2
    while ("$base ($suffix)".lowercase(Locale.ROOT) in occupied) suffix++
    return "$base ($suffix)"
}

internal fun normalizeWordlistLine(line: String): String? {
    require(!line.contains('\u0000')) { "Wordlist mengandung karakter NUL" }
    // readLine() already removes line endings. Every other character, including
    // leading/trailing spaces, can be part of a WPA passphrase and must survive.
    return line.takeIf { it.isNotEmpty() }
}

internal fun isValidWordlistFileName(fileName: String): Boolean {
    return fileName.matches(Regex("[A-Za-z0-9-]+\\.txt"))
}

private class SizeLimitedInputStream(
    private val delegate: InputStream,
    private val maxBytes: Long,
) : InputStream() {
    private var seen = 0L

    private fun add(count: Int) {
        if (count > 0) {
            seen += count
            if (seen > maxBytes) throw IOException("Ukuran wordlist melebihi 256 MB")
        }
    }

    override fun read(): Int {
        val value = delegate.read()
        if (value >= 0) add(1)
        return value
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val count = delegate.read(buffer, offset, length)
        add(count)
        return count
    }

    override fun close() = delegate.close()
}

internal object WordlistStore {
    private fun directory(context: Context): File {
        return File(context.filesDir, "wordlists").apply { mkdirs() }
    }

    private fun resolve(context: Context, fileName: String): File {
        require(isValidWordlistFileName(fileName)) { "Nama file wordlist tidak valid" }
        return File(directory(context), fileName)
    }

    fun openReader(context: Context, fileName: String): BufferedReader {
        return resolve(context, fileName).bufferedReader(Charsets.UTF_8)
    }

    fun delete(context: Context, fileName: String) {
        val file = resolve(context, fileName)
        if (file.exists() && !file.delete()) {
            throw IOException("File wordlist tidak dapat dihapus")
        }
    }

    fun exists(context: Context, fileName: String): Boolean {
        return resolve(context, fileName).isFile
    }

    fun import(context: Context, uri: Uri): ImportedWordlist {
        val resolver = context.contentResolver
        var displayName = "wordlist.txt"
        var declaredSize = -1L
        resolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0 && !cursor.isNull(nameIndex)) displayName = cursor.getString(nameIndex)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) declaredSize = cursor.getLong(sizeIndex)
            }
        }
        require(displayName.endsWith(".txt", ignoreCase = true)) { "Pilih file dengan ekstensi .txt" }
        require(declaredSize <= MAX_WORDLIST_BYTES) { "Ukuran wordlist melebihi 256 MB" }

        val target = File(directory(context), "${UUID.randomUUID()}.txt")
        try {
            val source = resolver.openInputStream(uri)
                ?: throw IOException("File wordlist tidak dapat dibuka")
            val stats = SizeLimitedInputStream(source, MAX_WORDLIST_BYTES)
                .bufferedReader(Charsets.UTF_8)
                .use { reader ->
                    target.bufferedWriter(Charsets.UTF_8).use { writer ->
                        copyNormalizedWordlist(reader, writer)
                    }
                }
            if (stats.count == 0) throw IOException("Wordlist tidak berisi entri")
            return ImportedWordlist(
                suggestedName = wordlistBaseName(displayName),
                fileName = target.name,
                count = stats.count,
                validCount = stats.validCount,
            )
        } catch (e: Exception) {
            target.delete()
            throw e
        }
    }
}
