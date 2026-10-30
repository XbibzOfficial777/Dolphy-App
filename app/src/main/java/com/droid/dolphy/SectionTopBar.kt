package com.droid.dolphy

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
internal val LocalSectionTopBarScrollBehavior = staticCompositionLocalOf<TopAppBarScrollBehavior?> { null }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    transparent: Boolean = false,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    alwaysCollapsed: Boolean = false,
    showRootBadge: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val scrollBehavior = LocalSectionTopBarScrollBehavior.current
    val liquid = isLiquidGlassTopBarChrome() && !transparent
    val titleContent: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
            if (showRootBadge) {
                Spacer(Modifier.width(6.dp))
                RootBadge(accentColor = accentColor)
            }
        }
    }
    val navigationContent: @Composable () -> Unit = {
        if (onBack != null) {
            DolphyIconButton(
                onClick = onBack,
                forTopBar = true,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = when {
            transparent || liquid -> Color.Transparent
            else -> MaterialTheme.colorScheme.background
        },
        scrolledContainerColor = when {
            transparent || liquid -> Color.Transparent
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    CenterAlignedTopAppBar(
        modifier = if (liquid) {
            Modifier
                .fillMaxWidth()
                .liquidGlassSurface(
                    shape = RectangleShape,
                    tint = MaterialTheme.colorScheme.surfaceContainerHigh,
                    variant = LiquidGlassVariant.Secondary,
                    containerAlpha = 0.82f,
                )
        } else Modifier.fillMaxWidth(),
        title = titleContent,
        navigationIcon = navigationContent,
        actions = actions,
        colors = colors,
        scrollBehavior = if (alwaysCollapsed) null else scrollBehavior,
    )
}
