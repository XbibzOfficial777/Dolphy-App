package com.kyant.backdrop.catalog.components

import org.junit.Assert.assertEquals
import org.junit.Test

class LiquidNavigationMathTest {
    @Test
    fun releasePositionSelectsNearestTab() {
        assertEquals(0, liquidTabTargetIndex(49f, 100f, 3))
        assertEquals(1, liquidTabTargetIndex(51f, 100f, 3))
        assertEquals(1, liquidTabTargetIndex(149f, 100f, 3))
        assertEquals(2, liquidTabTargetIndex(151f, 100f, 3))
    }

    @Test
    fun releasePositionIsClampedToAvailableTabs() {
        assertEquals(0, liquidTabTargetIndex(-500f, 100f, 3))
        assertEquals(2, liquidTabTargetIndex(500f, 100f, 3))
    }

    @Test
    fun invalidGeometryFallsBackToFirstTab() {
        assertEquals(0, liquidTabTargetIndex(100f, 0f, 3))
        assertEquals(0, liquidTabTargetIndex(Float.NaN, 100f, 3))
        assertEquals(0, liquidTabTargetIndex(100f, 100f, 0))
    }
}
