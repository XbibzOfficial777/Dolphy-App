package com.droid.dolphy

internal fun normalizedSliderFraction(value: Float, start: Float, endInclusive: Float): Float {
    if (!value.isFinite() || !start.isFinite() || !endInclusive.isFinite() || endInclusive <= start) {
        return 0f
    }
    return ((value - start) / (endInclusive - start)).coerceIn(0f, 1f)
}

internal fun sliderTrackThicknessDp(open: Float): Float {
    val restrainedOpen = open.coerceIn(0f, 1.2f)
    return 7f + 7f * restrainedOpen
}

internal fun sliderThumbRadiusDp(open: Float): Float {
    val restrainedOpen = open.coerceIn(0f, 1.2f)
    return 6f + 2f * restrainedOpen
}
