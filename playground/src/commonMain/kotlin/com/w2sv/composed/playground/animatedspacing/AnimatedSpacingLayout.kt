package com.w2sv.composed.playground.animatedspacing

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.ui.layout.AnimatedSpacingColumn
import com.w2sv.composed.ui.layout.AnimatedSpacingRow
import com.w2sv.composed.ui.layout.ExperimentalAnimatedSpacingApi

@Composable
@OptIn(ExperimentalAnimatedSpacingApi::class)
internal fun AnimatedSpacingLayout(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    visibility: AnimatedSpacingVisibility,
    onVisibilityChange: (AnimatedSpacingVisibility) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(PlaygroundDefaults.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
    ) {
        AnimatedSpacingConfigurationPanel(configuration, onConfigurationChange, Modifier.fillMaxWidth())
        VisibilityControls(visibility, onVisibilityChange)
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(Modifier.fillMaxSize().padding(PlaygroundDefaults.ContentPadding), contentAlignment = Alignment.Center) {
                when (configuration.orientation) {
                    AnimatedSpacingOrientation.Column -> AnimatedColumnPreview(configuration, visibility)
                    AnimatedSpacingOrientation.Row -> AnimatedRowPreview(configuration, visibility)
                }
            }
        }
    }
}

@Composable
private fun VisibilityControls(visibility: AnimatedSpacingVisibility, onVisibilityChange: (AnimatedSpacingVisibility) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)) {
        VisibilityChip("A", visibility.first) { onVisibilityChange(visibility.copy(first = !visibility.first)) }
        VisibilityChip("B", visibility.middle) { onVisibilityChange(visibility.copy(middle = !visibility.middle)) }
        VisibilityChip("C", visibility.last) { onVisibilityChange(visibility.copy(last = !visibility.last)) }
    }
}

@Composable
private fun VisibilityChip(
    label: String,
    visible: Boolean,
    onClick: () -> Unit
) {
    FilterChip(selected = visible, onClick = onClick, label = { Text("Item $label") })
}

@Composable
@OptIn(ExperimentalAnimatedSpacingApi::class)
private fun AnimatedColumnPreview(configuration: AnimatedSpacingConfiguration, visibility: AnimatedSpacingVisibility) {
    AnimatedSpacingColumn(
        spacing = configuration.spacingDp.dp,
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (configuration.weighted) {
            AnimatedVisibility(visibility.first, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("A", MaterialTheme.colorScheme.primaryContainer, Modifier.fillMaxSize())
            }
            AnimatedVisibility(visibility.middle, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("B", MaterialTheme.colorScheme.secondaryContainer, Modifier.fillMaxSize())
            }
            AnimatedVisibility(visibility.last, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("C", MaterialTheme.colorScheme.tertiaryContainer, Modifier.fillMaxSize())
            }
        } else {
            AnimatedVisibility(visibility.first, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("A", MaterialTheme.colorScheme.primaryContainer, Modifier.fillMaxWidth().height(72.dp))
            }
            AnimatedVisibility(visibility.middle, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("B", MaterialTheme.colorScheme.secondaryContainer, Modifier.fillMaxWidth().height(72.dp))
            }
            AnimatedVisibility(visibility.last, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("C", MaterialTheme.colorScheme.tertiaryContainer, Modifier.fillMaxWidth().height(72.dp))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalAnimatedSpacingApi::class)
private fun AnimatedRowPreview(configuration: AnimatedSpacingConfiguration, visibility: AnimatedSpacingVisibility) {
    AnimatedSpacingRow(
        spacing = configuration.spacingDp.dp,
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (configuration.weighted) {
            AnimatedVisibility(visibility.first, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("A", MaterialTheme.colorScheme.primaryContainer, Modifier.fillMaxSize())
            }
            AnimatedVisibility(visibility.middle, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("B", MaterialTheme.colorScheme.secondaryContainer, Modifier.fillMaxSize())
            }
            AnimatedVisibility(visibility.last, Modifier.weight(1f), configuration.animationSpec, configuration.fade) {
                DemoItem("C", MaterialTheme.colorScheme.tertiaryContainer, Modifier.fillMaxSize())
            }
        } else {
            AnimatedVisibility(visibility.first, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("A", MaterialTheme.colorScheme.primaryContainer, Modifier.width(120.dp).height(96.dp))
            }
            AnimatedVisibility(visibility.middle, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("B", MaterialTheme.colorScheme.secondaryContainer, Modifier.width(120.dp).height(96.dp))
            }
            AnimatedVisibility(visibility.last, animationSpec = configuration.animationSpec, fade = configuration.fade) {
                DemoItem("C", MaterialTheme.colorScheme.tertiaryContainer, Modifier.width(120.dp).height(96.dp))
            }
        }
    }
}

private val AnimatedSpacingConfiguration.animationSpec
    get() = tween<Float>(durationMillis = durationMillis, easing = FastOutSlowInEasing)

@Composable
private fun DemoItem(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
