package com.droid.dolphy

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLocaleTest {
    @Test
    fun indonesianIsTheDefaultForMissingOrUnknownValues() {
        assertEquals("in", AppLocale.normalizeLanguage(null))
        assertEquals("in", AppLocale.normalizeLanguage(""))
        assertEquals("in", AppLocale.normalizeLanguage("unknown"))
    }

    @Test
    fun allSupportedLanguagesArePreserved() {
        assertEquals("in", AppLocale.normalizeLanguage("in"))
        assertEquals("in", AppLocale.normalizeLanguage("id"))
        assertEquals("en", AppLocale.normalizeLanguage("en"))
        assertEquals("ru", AppLocale.normalizeLanguage("ru"))
    }

    @Test
    fun normalizationIsCaseInsensitiveAndTrimsWhitespace() {
        assertEquals("en", AppLocale.normalizeLanguage(" EN "))
        assertEquals("ru", AppLocale.normalizeLanguage(" RU "))
        assertEquals("in", AppLocale.normalizeLanguage(" ID "))
    }
}
