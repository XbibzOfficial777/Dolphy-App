package com.droid.dolphy.network

internal enum class WifiPersonalSecurity {
    WPA2,
    WPA3,
    UNSUPPORTED,
}

internal fun detectWifiPersonalSecurity(capabilities: String?): WifiPersonalSecurity {
    if (capabilities.isNullOrBlank()) return WifiPersonalSecurity.WPA2
    val caps = capabilities.uppercase()
    if ("EAP" in caps || "WAPI" in caps) return WifiPersonalSecurity.UNSUPPORTED
    val hasPsk = "PSK" in caps
    val hasSae = "SAE" in caps
    return when {
        hasSae && !hasPsk -> WifiPersonalSecurity.WPA3
        hasPsk -> WifiPersonalSecurity.WPA2
        else -> WifiPersonalSecurity.UNSUPPORTED
    }
}

internal fun isSupportedWpaPassphrase(password: String): Boolean {
    if (password.any { it.code < 32 || it.code > 126 }) return false
    if (password.length in 8..63) return true
    return password.length == 64 && password.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}

internal fun quoteLegacyWifiValue(value: String): String {
    return buildString(value.length + 2) {
        append('"')
        value.forEach { char ->
            if (char == '\\' || char == '"') append('\\')
            append(char)
        }
        append('"')
    }
}
