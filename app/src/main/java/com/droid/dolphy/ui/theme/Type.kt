package com.droid.dolphy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.droid.dolphy.R

private val AppFontFamily = FontFamily(Font(R.font.born2b_sporty_v2))
private val SystemSans = FontFamily.SansSerif

fun buildAppTypography(useFlipperFont: Boolean, fontScale: Float): Typography {
    val family = if (useFlipperFont) AppFontFamily else SystemSans
    val safeScale = if (useFlipperFont) fontScale.coerceIn(0.6f, 1.6f) else 1f
    val base = Typography()

    return base.copy(
        displayLarge = base.displayLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = base.displayLarge.fontSize * safeScale,
        ),
        displayMedium = base.displayMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = base.displayMedium.fontSize * safeScale,
        ),
        displaySmall = base.displaySmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = base.displaySmall.fontSize * safeScale,
        ),
        headlineLarge = base.headlineLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp * safeScale,
            lineHeight = 40.sp * safeScale,
        ),
        headlineMedium = base.headlineMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp * safeScale,
            lineHeight = 36.sp * safeScale,
        ),
        headlineSmall = base.headlineSmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp * safeScale,
            lineHeight = 32.sp * safeScale,
        ),
        titleLarge = base.titleLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp * safeScale,
            lineHeight = 28.sp * safeScale,
        ),
        titleMedium = base.titleMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp * safeScale,
            lineHeight = 24.sp * safeScale,
        ),
        titleSmall = base.titleSmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp * safeScale,
            lineHeight = 20.sp * safeScale,
        ),
        bodyLarge = base.bodyLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp * safeScale,
            lineHeight = 24.sp * safeScale,
        ),
        bodyMedium = base.bodyMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * safeScale,
            lineHeight = 20.sp * safeScale,
        ),
        bodySmall = base.bodySmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp * safeScale,
            lineHeight = 16.sp * safeScale,
        ),
        labelLarge = base.labelLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp * safeScale,
            lineHeight = 20.sp * safeScale,
        ),
        labelMedium = base.labelMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp * safeScale,
            lineHeight = 16.sp * safeScale,
        ),
        labelSmall = base.labelSmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp * safeScale,
            lineHeight = 16.sp * safeScale,
        ),
    )
}

fun buildExpressiveTypography(useFlipperFont: Boolean, fontScale: Float): Typography {
    val base = buildAppTypography(useFlipperFont, fontScale)
    return base.copy(
        displayLarge = base.displayLarge.copy(fontSize = base.displayLarge.fontSize * 1.08f),
        displayMedium = base.displayMedium.copy(fontSize = base.displayMedium.fontSize * 1.06f),
        displaySmall = base.displaySmall.copy(fontSize = base.displaySmall.fontSize * 1.04f),
        headlineLarge = base.headlineLarge.copy(fontSize = base.headlineLarge.fontSize * 1.04f),
        headlineMedium = base.headlineMedium.copy(fontSize = base.headlineMedium.fontSize * 1.03f),
    )
}

val Typography = buildAppTypography(useFlipperFont = false, fontScale = 1f)
