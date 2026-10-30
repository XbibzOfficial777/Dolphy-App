package com.droid.dolphy.network

import java.io.StringReader
import java.io.StringWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WordlistStoreTest {
    @Test
    fun lineNormalizationTrimsEntriesAndSkipsBlankLines() {
        assertEquals("password123", normalizeWordlistLine("  password123\t"))
        assertNull(normalizeWordlistLine(" \t "))
    }

    @Test(expected = IllegalArgumentException::class)
    fun lineNormalizationRejectsNul() {
        normalizeWordlistLine("bad\u0000password")
    }

    @Test
    fun streamingCopyNormalizesWithoutSplittingPasswordsOnWhitespace() {
        val output = StringWriter()
        val stats = copyNormalizedWordlist(
            StringReader("  first password  \n\nshort\nsecond123\n").buffered(),
            output,
        )

        assertEquals("first password\nshort\nsecond123\n", output.toString())
        assertEquals(3, stats.count)
        assertEquals(2, stats.validCount)
    }

    @Test
    fun suggestedNamesRemoveOnlyTheFinalTxtExtension() {
        assertEquals("my.wordlist", wordlistBaseName("folder/my.wordlist.TXT"))
        assertEquals("Wordlist", wordlistBaseName(".txt"))
        assertEquals("wordlist", wordlistBaseName("wordlist"))
    }

    @Test
    fun duplicateGroupNamesGetCaseInsensitiveSuffixes() {
        val existing = listOf("Default", "My List", "my list (2)")
        assertEquals("my list (3)", uniqueWordlistName("my list", existing))
        assertEquals("New List", uniqueWordlistName("New List", existing))
    }

    @Test
    fun generatedFileNamesRejectTraversalAndUnexpectedExtensions() {
        assertTrue(isValidWordlistFileName("6a61df7c-0a6b-4f0d-928f-efdb50dc6c8a.txt"))
        assertFalse(isValidWordlistFileName("../secrets.txt"))
        assertFalse(isValidWordlistFileName("wordlist.json"))
        assertFalse(isValidWordlistFileName("wordlist.TXT"))
    }
}
