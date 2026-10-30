package com.droid.dolphy

import org.junit.Assert.assertEquals
import org.junit.Test

class SliderMotionMathTest {
    @Test
    fun normalizesValueInsideRange() {
        assertEquals(0.5f, normalizedSliderFraction(5f, 0f, 10f), 0.0001f)
        assertEquals(0.25f, normalizedSliderFraction(10f, 0f, 40f), 0.0001f)
    }

    @Test
    fun clampsValuesToTrackEndpoints() {
        assertEquals(0f, normalizedSliderFraction(-2f, 0f, 10f), 0.0001f)
        assertEquals(1f, normalizedSliderFraction(12f, 0f, 10f), 0.0001f)
    }

    @Test
    fun invalidRangesFallBackToStart() {
        assertEquals(0f, normalizedSliderFraction(1f, 1f, 1f), 0.0001f)
        assertEquals(0f, normalizedSliderFraction(1f, 2f, 1f), 0.0001f)
        assertEquals(0f, normalizedSliderFraction(Float.NaN, 0f, 1f), 0.0001f)
    }

    @Test
    fun heldStateExpandsTrackAndThumb() {
        assertEquals(7f, sliderTrackThicknessDp(0f), 0.0001f)
        assertEquals(14f, sliderTrackThicknessDp(1f), 0.0001f)
        assertEquals(6f, sliderThumbRadiusDp(0f), 0.0001f)
        assertEquals(8f, sliderThumbRadiusDp(1f), 0.0001f)
    }
}
