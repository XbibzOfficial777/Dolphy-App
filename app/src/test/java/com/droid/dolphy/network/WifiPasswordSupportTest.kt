package com.droid.dolphy.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WifiPasswordSupportTest {
    @Test
    fun scanCapabilitiesSelectSupportedPersonalSecurity() {
        assertEquals(WifiPersonalSecurity.WPA2, detectWifiPersonalSecurity("[WPA2-PSK-CCMP][ESS]"))
        assertEquals(WifiPersonalSecurity.WPA2, detectWifiPersonalSecurity("[RSN-PSK+SAE-CCMP][ESS]"))
        assertEquals(WifiPersonalSecurity.WPA3, detectWifiPersonalSecurity("[RSN-SAE-CCMP][ESS]"))
        assertEquals(WifiPersonalSecurity.UNSUPPORTED, detectWifiPersonalSecurity("[WPA2-EAP-CCMP][ESS]"))
        assertEquals(WifiPersonalSecurity.UNSUPPORTED, detectWifiPersonalSecurity("[ESS]"))
    }

    @Test
    fun passphraseValidationPreservesLegalSpacesAndRejectsInvalidValues() {
        assertTrue(isSupportedWpaPassphrase("        "))
        assertTrue(isSupportedWpaPassphrase("  correct password  "))
        assertFalse(isSupportedWpaPassphrase("1234567"))
        assertTrue(isSupportedWpaPassphrase("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"))
        assertFalse(isSupportedWpaPassphrase("g".repeat(64)))
        assertFalse(isSupportedWpaPassphrase("a".repeat(65)))
    }

    @Test
    fun legacyConfigurationQuotingEscapesQuotesAndBackslashes() {
        assertEquals("\"  pa\\\"ss\\\\word  \"", quoteLegacyWifiValue("  pa\"ss\\word  "))
    }
}
