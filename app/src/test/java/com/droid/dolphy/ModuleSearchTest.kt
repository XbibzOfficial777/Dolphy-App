package com.droid.dolphy

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModuleSearchTest {
    @Test
    fun blankQueryMatchesEverything() {
        assertTrue(moduleMatchesQuery("BLE Spam", "Mengirim iklan", "Bluetooth", ""))
        assertTrue(moduleMatchesQuery("BLE Spam", "Mengirim iklan", "Bluetooth", "   "))
    }

    @Test
    fun queryIgnoresCaseAndMatchesDescription() {
        assertTrue(moduleMatchesQuery("Pemindai NFC", "Membaca dan menulis tag", "Lainnya", "nfc"))
        assertTrue(moduleMatchesQuery("Pemindai NFC", "Membaca dan menulis tag", "Lainnya", "MEMBACA"))
    }

    @Test
    fun everyTokenMustMatchInAnyOrder() {
        assertTrue(moduleMatchesQuery("Pemindai NFC", "Membaca tag", "Lainnya", "tag nfc"))
        assertFalse(moduleMatchesQuery("Pemindai NFC", "Membaca tag", "Lainnya", "tag kamera"))
    }

    @Test
    fun sectionNameIsSearchable() {
        assertTrue(moduleMatchesQuery("Sub-GHz", "Remote nirkabel", "Radio", "radio sub"))
    }

    @Test
    fun unknownQueryDoesNotMatch() {
        assertFalse(moduleMatchesQuery("Kamera", "Remote rana", "Lainnya", "bluetooth"))
    }

    @Test
    fun filteringRemovesUnmatchedModulesAndEmptySections() {
        fun destination(title: String, route: String, section: String) = FunctionDestination(
            icon = Icons.Default.Search,
            title = title,
            description = "Deskripsi $title",
            route = route,
            section = section,
        )

        val sections = listOf(
            "Bluetooth" to listOf(
                destination("BLE Spam", "ble", "Bluetooth"),
                destination("Bond Spam", "bond", "Bluetooth"),
            ),
            "Radio" to listOf(destination("Sub-GHz", "subghz", "Radio")),
        )

        val filtered = filterFunctionDestinationSections(sections, "bluetooth ble")
        assertEquals(listOf("Bluetooth"), filtered.map { it.first })
        assertEquals(listOf("ble"), filtered.single().second.map { it.route })
    }
}
