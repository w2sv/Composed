package com.w2sv.composed.playground.animatedspacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.shared.LabeledChoices
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.PlaygroundVerticalScrollbar
import com.w2sv.composed.playground.shared.SampleConfigurationCard
import com.w2sv.composed.playground.shared.SampleControlPair
import com.w2sv.composed.playground.shared.SampleControlSection
import com.w2sv.composed.ui.layout.AnimatedSpacingColumn
import kotlin.math.roundToInt

@Composable
internal fun AnimatedSpacingConfigurationPanel(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    scrollable: Boolean = false
) {
    val scrollState = rememberScrollState()
    SampleConfigurationCard(
        title = "Animated spacing layouts",
        description = "Toggle items below and compare fixed sizing with animated weight redistribution in a row or column.",
        modifier = modifier,
        contentModifier = if (scrollable) {
            Modifier
                .verticalScroll(scrollState)
                .padding(end = PlaygroundDefaults.ScrollbarThickness + PlaygroundDefaults.ScrollbarEdgePadding)
        } else {
            Modifier
        },
        contentOverlay = if (scrollable) {
            {
                PlaygroundVerticalScrollbar(
                    state = scrollState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(vertical = PlaygroundDefaults.ScrollbarEdgePadding)
                        .width(PlaygroundDefaults.ScrollbarThickness)
                )
            }
        } else {
            null
        },
        showHeader = false
    ) {
        if (compact) {
            CompactControls(configuration, onConfigurationChange, onReset)
        } else {
            StandardControls(configuration, onConfigurationChange, onReset)
        }
    }
}

@Composable
private fun StandardControls(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    onReset: () -> Unit
) {
    SampleControlPair(
        first = {
            Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
                LayoutControls(configuration, onConfigurationChange, onReset)
                AnchorControls(configuration, onConfigurationChange)
            }
        },
        second = { AnimationControls(configuration, onConfigurationChange) }
    )
}

@Composable
private fun CompactControls(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    onReset: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)) {
        Box(Modifier.weight(1f)) { LayoutControls(configuration, onConfigurationChange, onReset) }
        Box(Modifier.weight(1f)) { AnchorControls(configuration, onConfigurationChange) }
        Box(Modifier.weight(1f)) {
            SampleControlSection("Appearance") {
                LabeledChoices(
                    label = "Animation",
                    values = AnimatedSpacingAnimation.entries,
                    selected = configuration.animation,
                    onSelected = { onConfigurationChange(configuration.copy(animation = it)) },
                    valueLabel = AnimatedSpacingAnimation::label
                )
                LabeledChoices(
                    label = "Fade",
                    values = listOf(true, false),
                    selected = configuration.fade,
                    onSelected = { onConfigurationChange(configuration.copy(fade = it)) },
                    valueLabel = { if (it) "On" else "Off" }
                )
                SliderControls(configuration, onConfigurationChange)
            }
        }
    }
}

@Composable
private fun LayoutControls(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    onReset: () -> Unit
) {
    SampleControlSection("Layout") {
        LabeledChoices(
            label = "Orientation",
            values = AnimatedSpacingOrientation.entries,
            selected = configuration.orientation,
            onSelected = { onConfigurationChange(configuration.copy(orientation = it)) },
            valueLabel = AnimatedSpacingOrientation::label
        )
        LabeledChoices(
            label = "Sizing",
            values = listOf(false, true),
            selected = configuration.weighted,
            onSelected = { onConfigurationChange(configuration.copy(weighted = it)) },
            valueLabel = { if (it) "Weighted" else "Fixed" }
        )
        ParameterSlider(
            label = "Spacing",
            valueLabel = "${configuration.spacingDp} dp",
            value = configuration.spacingDp.toFloat(),
            valueRange = 0f..64f,
            onValueChange = { onConfigurationChange(configuration.copy(spacingDp = it.roundToInt())) }
        )
        OutlinedButton(onClick = onReset) { Text("Reset") }
    }
}

@Composable
private fun AnchorControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
    SampleControlSection("Anchors") {
        LabeledChoices(
            label = "Expand from",
            values = AnimatedSpacingAnchor.entries,
            selected = configuration.expandFrom,
            onSelected = { onConfigurationChange(configuration.copy(expandFrom = it)) },
            valueLabel = AnimatedSpacingAnchor::label
        )
        LabeledChoices(
            label = "Shrink towards",
            values = AnimatedSpacingAnchor.entries,
            selected = configuration.shrinkTowards,
            onSelected = { onConfigurationChange(configuration.copy(shrinkTowards = it)) },
            valueLabel = AnimatedSpacingAnchor::label
        )
    }
}

@Composable
private fun AnimationControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
        SampleControlSection("Animation") {
            LabeledChoices(
                label = "Spec",
                values = AnimatedSpacingAnimation.entries,
                selected = configuration.animation,
                onSelected = { onConfigurationChange(configuration.copy(animation = it)) },
                valueLabel = AnimatedSpacingAnimation::label
            )
            LabeledChoices(
                label = "Fade",
                values = listOf(true, false),
                selected = configuration.fade,
                onSelected = { onConfigurationChange(configuration.copy(fade = it)) },
                valueLabel = { if (it) "On" else "Off" }
            )
            SliderControls(configuration, onConfigurationChange)
        }
    }
}

@Composable
private fun SliderControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
    AnimatedSpacingColumn(spacing = PlaygroundDefaults.ControlSpacing) {
        AnimatedVisibility(configuration.animation == AnimatedSpacingAnimation.Tween) {
            ParameterSlider(
                label = "Duration",
                valueLabel = "${configuration.durationMillis} ms",
                value = configuration.durationMillis.toFloat(),
                valueRange = 100f..1_500f,
                onValueChange = { onConfigurationChange(configuration.copy(durationMillis = it.roundToInt())) }
            )
        }
    }
}
