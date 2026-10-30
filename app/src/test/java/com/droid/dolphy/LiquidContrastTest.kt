package com.droid.dolphy

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiquidContrastTest {
    private fun contrastRatio(first: Color, second: Color): Float {
        val lighter = maxOf(first.luminance(), second.luminance())
        val darker = minOf(first.luminance(), second.luminance())
        return (lighter + 0.05f) / (darker + 0.05f)
    }

    @Test
    fun defaultAccentUsesDarkContent() {
        assertEquals(Color(0xFF1C1C1F), contrastingContentColor(LiquidColors.Accent))
    }

    @Test
    fun defaultAccentAndSecondaryMeetTextContrast() {
        listOf(LiquidColors.Accent, LiquidColors.AccentSecondary).forEach { accent ->
            val content = contrastingContentColor(accent)
            assertTrue(contrastRatio(accent, content) >= 4.5f)
        }
    }

    @Test
    fun statusErrorRetainsReadableContent() {
        val content = contrastingContentColor(LiquidColors.Error)
        assertTrue(contrastRatio(LiquidColors.Error, content) >= 4.5f)
    }
}
