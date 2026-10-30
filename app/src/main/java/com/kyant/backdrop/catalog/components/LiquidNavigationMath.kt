package com.kyant.backdrop.catalog.components

import kotlin.math.roundToInt

internal fun liquidTabTargetIndex(
    indicatorPositionPx: Float,
    tabWidthPx: Float,
    tabsCount: Int,
): Int {
    if (tabsCount <= 0 || tabWidthPx <= 0f || !tabWidthPx.isFinite() || !indicatorPositionPx.isFinite()) {
        return 0
    }
    return (indicatorPositionPx / tabWidthPx)
        .roundToInt()
        .coerceIn(0, tabsCount - 1)
}
