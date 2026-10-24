package com.droid.dolphy

internal data class IrAssetLocation(
    val key: String,
    val category: String,
    val brand: String,
    val model: String,
    val assetPath: String,
)

/** Maps both flat and deeply nested IRDB asset paths to a navigable category/brand/model entry. */
internal fun flipperAssetLocation(assetPath: String): IrAssetLocation? {
    val normalized = assetPath.trim('/')
    val parts = normalized.split('/').filter { it.isNotBlank() }
    if (parts.size < 3 || parts.first() != "Flipper-IRDB-main") return null
    if (parts.any { it == ".git" || it == ".github" }) return null

    val fileName = parts.last()
    if (!fileName.endsWith(".ir", ignoreCase = true)) return null
    val category = parts[1]
    if (category.startsWith('.')) return null
    val model = fileName.substringBeforeLast('.').trim()
    if (category.isBlank() || model.isBlank()) return null

    val brand = if (parts.size == 3) {
        "Uncategorized"
    } else {
        parts.subList(2, parts.lastIndex).joinToString("/")
    }
    if (brand.isBlank()) return null
    return IrAssetLocation(
        key = "$category/$brand",
        category = category,
        brand = brand,
        model = model,
        assetPath = normalized,
    )
}

internal fun buildFlipperIndex(assetPaths: Iterable<String>): Map<String, Map<String, String>> {
    val result = linkedMapOf<String, MutableMap<String, String>>()
    assetPaths.forEach { path ->
        val location = flipperAssetLocation(path) ?: return@forEach
        result.getOrPut(location.key) { linkedMapOf() }[location.model] = location.assetPath
    }
    return result
}
