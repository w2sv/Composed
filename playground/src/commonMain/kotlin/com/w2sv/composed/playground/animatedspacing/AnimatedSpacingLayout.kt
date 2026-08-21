package com.w2sv.composed.playground.animatedspacing

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.w2sv.composed.ui.layout.AnimatedSpacingColumnAnimation
import com.w2sv.composed.ui.layout.AnimatedSpacingRow
import com.w2sv.composed.ui.layout.AnimatedSpacingRowAnimation

@Composable
internal fun AnimatedSpacingLayout(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    visibility: AnimatedSpacingVisibility,
    onVisibilityChange: (AnimatedSpacingVisibility) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (configuration.orientation) {
        AnimatedSpacingOrientation.Column -> Row(
            modifier = modifier.padding(PlaygroundDefaults.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)
            ) {
                AnimatedSpacingConfigurationPanel(
                    configuration = configuration,
                    onConfigurationChange = onConfigurationChange,
                    onReset = onReset,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    scrollable = true
                )
                VisibilityControls(visibility, onVisibilityChange)
            }

            PreviewCard(Modifier.fillMaxHeight().weight(1f)) {
                AnimatedColumnPreview(configuration, visibility, onVisibilityChange)
            }
        }

        AnimatedSpacingOrientation.Row -> Column(
            modifier = modifier.padding(PlaygroundDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
        ) {
            AnimatedSpacingConfigurationPanel(
                configuration = configuration,
                onConfigurationChange = onConfigurationChange,
                onReset = onReset,
                modifier = Modifier.fillMaxWidth(),
                compact = true
            )
            VisibilityControls(visibility, onVisibilityChange)
            PreviewCard(Modifier.fillMaxWidth().weight(1f)) {
                AnimatedRowPreview(configuration, visibility, onVisibilityChange)
            }
        }
    }
}

@Composable
private fun PreviewCard(modifier: Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun VisibilityControls(visibility: AnimatedSpacingVisibility, onVisibilityChange: (AnimatedSpacingVisibility) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)) {
            visibility.items.forEachIndexed { index, visible ->
                VisibilityChip(itemLabel(index), visible) { onVisibilityChange(visibility.toggle(index)) }
            }
            Button(
                onClick = {
                    onVisibilityChange(
                        if (visibility.allVisible) AnimatedSpacingVisibility.none() else AnimatedSpacingVisibility()
                    )
                }
            ) {
                Text(if (visibility.allVisible) "Hide all" else "Show all")
            }
        }
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
private fun AnimatedColumnPreview(
    configuration: AnimatedSpacingConfiguration,
    visibility: AnimatedSpacingVisibility,
    onVisibilityChange: (AnimatedSpacingVisibility) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val listModifier = if (configuration.weighted) {
            Modifier.fillMaxWidth().height(maxHeight)
        } else {
            Modifier.fillMaxWidth().heightIn(min = maxHeight)
        }
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            AnimatedSpacingColumn(
                spacing = configuration.spacingDp.dp,
                modifier = listModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                animation = AnimatedSpacingColumnAnimation(
                    animationSpec = configuration.animationSpec,
                    fade = configuration.fade,
                    expandFrom = configuration.expandFrom.verticalAlignment,
                    shrinkTowards = configuration.shrinkTowards.verticalAlignment
                )
            ) {
                visibility.items.forEachIndexed { index, visible ->
                    AnimatedVisibility(
                        visible = visible,
                        modifier = if (configuration.weighted) Modifier.weight(1f) else Modifier
                    ) {
                        DemoItem(
                            label = itemLabel(index),
                            containerColor = itemContainerColor(index),
                            contentColor = itemContentColor(index),
                            onClick = { onVisibilityChange(visibility.toggle(index)) },
                            modifier = if (configuration.weighted) Modifier.fillMaxSize() else Modifier.fillMaxWidth().height(72.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedRowPreview(
    configuration: AnimatedSpacingConfiguration,
    visibility: AnimatedSpacingVisibility,
    onVisibilityChange: (AnimatedSpacingVisibility) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val listModifier = if (configuration.weighted) {
            Modifier.width(maxWidth).fillMaxHeight()
        } else {
            Modifier.widthIn(min = maxWidth).fillMaxHeight()
        }
        Box(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
            AnimatedSpacingRow(
                spacing = configuration.spacingDp.dp,
                modifier = listModifier,
                verticalAlignment = Alignment.CenterVertically,
                animation = AnimatedSpacingRowAnimation(
                    animationSpec = configuration.animationSpec,
                    fade = configuration.fade,
                    expandFrom = configuration.expandFrom.horizontalAlignment,
                    shrinkTowards = configuration.shrinkTowards.horizontalAlignment
                )
            ) {
                visibility.items.forEachIndexed { index, visible ->
                    AnimatedVisibility(
                        visible = visible,
                        modifier = if (configuration.weighted) Modifier.weight(1f) else Modifier
                    ) {
                        DemoItem(
                            label = itemLabel(index),
                            containerColor = itemContainerColor(index),
                            contentColor = itemContentColor(index),
                            onClick = { onVisibilityChange(visibility.toggle(index)) },
                            modifier = if (configuration.weighted) Modifier.fillMaxSize() else Modifier.width(120.dp).height(96.dp)
                        )
                    }
                }
            }
        }
    }
}

private val AnimatedSpacingConfiguration.animationSpec: FiniteAnimationSpec<Float>
    get() = when (animation) {
        AnimatedSpacingAnimation.Tween -> tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
        AnimatedSpacingAnimation.Spring -> spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    }

@Composable
private fun DemoItem(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun itemContainerColor(index: Int) =
    when (index) {
        0 -> MaterialTheme.colorScheme.primary
        1 -> MaterialTheme.colorScheme.secondary
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

@Composable
private fun itemContentColor(index: Int) =
    when (index) {
        0 -> MaterialTheme.colorScheme.onPrimary
        1 -> MaterialTheme.colorScheme.onSecondary
        2 -> MaterialTheme.colorScheme.onTertiary
        else -> MaterialTheme.colorScheme.onError
    }

private fun itemLabel(index: Int) =
    ('A'.code + index).toChar().toString()

private val AnimatedSpacingAnchor.verticalAlignment
    get() = when (this) {
        AnimatedSpacingAnchor.Start -> Alignment.Top
        AnimatedSpacingAnchor.Center -> Alignment.CenterVertically
        AnimatedSpacingAnchor.End -> Alignment.Bottom
    }

private val AnimatedSpacingAnchor.horizontalAlignment
    get() = when (this) {
        AnimatedSpacingAnchor.Start -> Alignment.Start
        AnimatedSpacingAnchor.Center -> Alignment.CenterHorizontally
        AnimatedSpacingAnchor.End -> Alignment.End
    }
