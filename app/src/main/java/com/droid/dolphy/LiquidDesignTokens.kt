package com.droid.dolphy

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LiquidColors {
    val Background = Color(0xFF101114)
    val Surface = Color(0xFF191B20)
    val ElevatedSurface = Color(0xFF22252B)
    val ElevatedSurfaceHigh = Color(0xFF292D34)
    val TextPrimary = Color(0xFFF5F5F7)
    val TextSecondary = Color(0xFFA5A7AF)
    val TextMuted = Color(0xFF777B85)
    val Accent = Color(0xFFA78BFA)
    val AccentSecondary = Color(0xFF8B9CF6)
    val Success = Color(0xFF65C995)
    val Warning = Color(0xFFE8B66D)
    val Error = Color(0xFFEF7777)
    val DarkGlass = Color(0xFF141418)

    val LightBackground = Color(0xFFF7F6F4)
    val LightSurface = Color(0xFFFFFFFF)
    val LightElevatedSurface = Color(0xFFF0EEEB)
    val LightTextPrimary = Color(0xFF1C1C1F)
    val LightTextSecondary = Color(0xFF5F6067)
}

object LiquidSpacing {
    val Micro = 4.dp
    val Tight = 8.dp
    val Small = 12.dp
    val Standard = 16.dp
    val Medium = 20.dp
    val Large = 24.dp
    val Section = 32.dp
    val Major = 40.dp
    val Layout = 48.dp
}

object LiquidRadius {
    val Control = 8.dp
    val Standard = 12.dp
    val Card = 16.dp
    val LargeCard = 20.dp
    val Panel = 24.dp
    val MajorSurface = 28.dp
}

object LiquidSizing {
    val TouchTarget = 48.dp
    val ButtonHeight = 56.dp
    val NavigationHeight = 64.dp
    val NavigationIcon = 22.dp
}

object LiquidMotion {
    const val ButtonMillis = 120
    const val IconMillis = 180
    const val StateMillis = 200
    const val CardMillis = 260
    const val ScreenMillis = 340
    const val SheetMillis = 380
    const val PressScale = 0.98f
    const val SelectedIconScale = 1.06f
    const val SpringDamping = 0.80f
    const val SpringStiffness = 480f
}

enum class LiquidGlassVariant {
    Primary,
    Secondary,
    Dark,
}
