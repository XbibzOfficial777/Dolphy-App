package com.droid.dolphy.tvcast

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CastMediaServerTest {
    @Test
    fun noRangeRequestsWholeRepresentation() {
        assertNull(parseByteRange(null, 1000))
        assertNull(parseByteRange("", 1000))
    }

    @Test
    fun boundedAndOpenEndedRangesAreParsed() {
        assertEquals(0L..99L, parseByteRange("bytes=0-99", 1000))
        assertEquals(100L..999L, parseByteRange("bytes=100-", 1000))
        assertEquals(100L..999L, parseByteRange("bytes=100-5000", 1000))
    }

    @Test
    fun suffixRangeUsesEndOfMedia() {
        assertEquals(900L..999L, parseByteRange("bytes=-100", 1000))
        assertEquals(0L..999L, parseByteRange("bytes=-5000", 1000))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rangeStartBeyondMediaIsRejected() {
        parseByteRange("bytes=1000-1001", 1000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun multipleRangesAreRejectedInsteadOfCorruptingPlayback() {
        parseByteRange("bytes=0-10,20-30", 1000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptySuffixRangeIsRejected() {
        parseByteRange("bytes=-0", 1000)
    }
}
