package com.droid.dolphy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/**
 * Shared lightweight glass material. It deliberately avoids a full-screen
 * real-time blur: translucent depth, a quiet reflection and a delicate border
 * remain attractive on devices where backdrop blur is unavailable or costly.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape,
    tint: Color,
    variant: LiquidGlassVariant = LiquidGlassVariant.Secondary,
    containerAlpha: Float? = null,
): Modifier {
    val alpha = containerAlpha ?: when (variant) {
        LiquidGlassVariant.Primary -> 0.68f
        LiquidGlassVariant.Secondary -> 0.74f
        LiquidGlassVariant.Dark -> 0.58f
    }
    val borderAlpha = when (variant) {
        LiquidGlassVariant.Primary -> 0.20f
        LiquidGlassVariant.Secondary -> 0.12f
        LiquidGlassVariant.Dark -> 0.10f
    }
    val reflectionAlpha = when (variant) {
        LiquidGlassVariant.Primary -> 0.10f
        LiquidGlassVariant.Secondary -> 0.06f
        LiquidGlassVariant.Dark -> 0.08f
    }
    val elevation = when (variant) {
        LiquidGlassVariant.Primary -> 8.dp
        LiquidGlassVariant.Secondary -> 3.dp
        LiquidGlassVariant.Dark -> 10.dp
    }
    val base = if (variant == LiquidGlassVariant.Dark) LiquidColors.DarkGlass else tint
    val outline = if (base.luminance() > 0.45f) Color(0xFF1C1C1F) else Color.White

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = tint.copy(alpha = 0.05f),
            spotColor = Color.Black.copy(alpha = 0.22f),
        )
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(
                    base.copy(alpha = alpha),
                    base.copy(alpha = alpha * 0.82f),
                )
            )
        )
        .drawBehind {
            drawRect(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = reflectionAlpha),
                        Color.White.copy(alpha = reflectionAlpha * 0.18f),
                        Color.Transparent,
                    )
                )
            )
        }
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                listOf(
                    outline.copy(alpha = borderAlpha),
                    outline.copy(alpha = borderAlpha * 0.55f),
                )
            ),
            shape = shape,
        )
}
