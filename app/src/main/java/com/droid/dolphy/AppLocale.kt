package com.droid.dolphy

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale






object AppLocale {
    fun normalizeLanguage(language: String?): String {
        return when (language?.trim()?.lowercase(Locale.ROOT)) {
            "en" -> "en"
            "ru" -> "ru"
            else -> "in"
        }
    }

    fun wrap(context: Context): Context {
        val prefs = context.getSharedPreferences("DolphyPrefs", Context.MODE_PRIVATE)
        val language = normalizeLanguage(prefs.getString("app_language", "in"))
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocales(android.os.LocaleList(locale))

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}

