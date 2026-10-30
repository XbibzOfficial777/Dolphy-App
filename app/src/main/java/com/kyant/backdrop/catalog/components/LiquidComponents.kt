package com.kyant.backdrop.catalog.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.droid.dolphy.LiquidGlassVariant
import com.droid.dolphy.LiquidMotion
import com.droid.dolphy.LiquidRadius
import com.droid.dolphy.LiquidSizing
import com.droid.dolphy.LocalReducedMotionEnabled
import com.droid.dolphy.contrastingContentColor
import com.droid.dolphy.liquidGlassSurface
import com.kyant.backdrop.Backdrop
import kotlinx.coroutines.launch

private fun Modifier.liquidSurface(
    shape: Shape,
    tint: Color,
    containerAlpha: Float? = null,
    variant: LiquidGlassVariant = LiquidGlassVariant.Secondary,
): Modifier = liquidGlassSurface(
    shape = shape,
    tint = tint,
    variant = variant,
    containerAlpha = containerAlpha,
)

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    surfaceColor: Color = tint,
    applyDefaultHeight: Boolean = true,
    contentPaddingHorizontal: Dp = 24.dp,
    isInteractive: Boolean = true,
    motionEnabled: Boolean = !LocalReducedMotionEnabled.current,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && isInteractive && motionEnabled) LiquidMotion.PressScale else 1f,
        animationSpec = spring(
            dampingRatio = LiquidMotion.SpringDamping,
            stiffness = LiquidMotion.SpringStiffness,
        ),
        label = "liquid_button_press",
    )
    val heightModifier = if (applyDefaultHeight) Modifier.defaultMinSize(minHeight = LiquidSizing.ButtonHeight) else Modifier
    val contentColor = contrastingContentColor(surfaceColor)
    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(heightModifier)
            .liquidSurface(
                RoundedCornerShape(LiquidRadius.MajorSurface),
                surfaceColor,
                containerAlpha = 0.84f,
                variant = LiquidGlassVariant.Primary,
            )
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = contentPaddingHorizontal, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally,
        ),
        content = {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        },
    )
}

@Composable
fun LiquidBottomTabs(
    selectedTabIndex: () -> Int,
    onTabSelected: (Int) -> Unit,
    backdrop: Backdrop,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    barHeight: Dp = LiquidSizing.NavigationHeight,
    motionEnabled: Boolean = !LocalReducedMotionEnabled.current,
    content: @Composable RowScope.() -> Unit,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val indicatorPosition = remember { Animatable(0f) }
    var indicatorInitialized by remember { mutableStateOf(false) }
    var dragging by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .height(barHeight)
            .liquidSurface(
                RoundedCornerShape(barHeight / 2),
                MaterialTheme.colorScheme.surfaceContainerHigh,
                containerAlpha = 0.64f,
                variant = LiquidGlassVariant.Dark,
            )
    ) {
        val safeTabCount = tabsCount.coerceAtLeast(1)
        val selectedIndex = selectedTabIndex().coerceIn(0, safeTabCount - 1)
        val currentSelectedIndex by rememberUpdatedState(selectedIndex)
        val currentOnTabSelected by rememberUpdatedState(onTabSelected)
        val tabWidth = maxWidth / safeTabCount
        val tabWidthPx = with(density) { tabWidth.toPx() }
        val maxIndicatorPosition = (safeTabCount - 1) * tabWidthPx

        LaunchedEffect(selectedIndex, tabWidthPx, safeTabCount, motionEnabled) {
            val target = selectedIndex * tabWidthPx
            if (!indicatorInitialized) {
                indicatorPosition.snapTo(target)
                indicatorInitialized = true
            } else if (!dragging) {
                if (motionEnabled) {
                    indicatorPosition.animateTo(
                        targetValue = target,
                        animationSpec = spring(
                            dampingRatio = LiquidMotion.SpringDamping,
                            stiffness = 440f,
                        ),
                    )
                } else {
                    indicatorPosition.snapTo(target)
                }
            }
        }

        val indicatorScaleX by animateFloatAsState(
            targetValue = if (dragging && motionEnabled) 1.04f else 1f,
            animationSpec = spring(dampingRatio = 0.82f, stiffness = 480f),
            label = "liquid_tab_stretch_x",
        )
        val indicatorScaleY by animateFloatAsState(
            targetValue = if (dragging && motionEnabled) 0.98f else 1f,
            animationSpec = spring(dampingRatio = 0.82f, stiffness = 480f),
            label = "liquid_tab_stretch_y",
        )
        val indicatorOffset = with(density) { indicatorPosition.value.toDp() }

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    scaleX = indicatorScaleX
                    scaleY = indicatorScaleY
                }
                .padding(4.dp)
                .liquidSurface(
                    CircleShape,
                    MaterialTheme.colorScheme.primary,
                    containerAlpha = 0.90f,
                    variant = LiquidGlassVariant.Primary,
                )
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(safeTabCount, tabWidthPx, motionEnabled) {
                    if (tabWidthPx <= 0f) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragging = true
                            scope.launch { indicatorPosition.stop() }
                        },
                        onDragEnd = {
                            val targetIndex = liquidTabTargetIndex(
                                indicatorPositionPx = indicatorPosition.value,
                                tabWidthPx = tabWidthPx,
                                tabsCount = safeTabCount,
                            )
                            dragging = false
                            scope.launch {
                                if (motionEnabled) {
                                    indicatorPosition.animateTo(
                                        targetValue = targetIndex * tabWidthPx,
                                        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
                                    )
                                } else {
                                    indicatorPosition.snapTo(targetIndex * tabWidthPx)
                                }
                            }
                            currentOnTabSelected(targetIndex)
                        },
                        onDragCancel = {
                            dragging = false
                            scope.launch {
                                if (motionEnabled) {
                                    indicatorPosition.animateTo(
                                        targetValue = currentSelectedIndex * tabWidthPx,
                                        animationSpec = spring(dampingRatio = 0.82f, stiffness = 440f),
                                    )
                                } else {
                                    indicatorPosition.snapTo(currentSelectedIndex * tabWidthPx)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val nextPosition = (indicatorPosition.value + dragAmount)
                                .coerceIn(0f, maxIndicatorPosition)
                            scope.launch { indicatorPosition.snapTo(nextPosition) }
                        },
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun RowScope.LiquidBottomTab(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun LiquidToggle(
    selected: () -> Boolean,
    onSelect: (Boolean) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Switch(
        checked = selected(),
        onCheckedChange = onSelect,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = contrastingContentColor(accentColor),
            checkedTrackColor = accentColor.copy(alpha = 0.82f),
            checkedBorderColor = Color.White.copy(alpha = 0.16f),
        ),
    )
}

@Composable
fun LiquidSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    visibilityThreshold: Float,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Slider(
        value = value(),
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = accentColor,
            activeTrackColor = accentColor.copy(alpha = 0.82f),
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.64f),
        ),
    )
}
