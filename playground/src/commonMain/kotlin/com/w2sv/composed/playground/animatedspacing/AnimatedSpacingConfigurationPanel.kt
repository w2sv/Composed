package com.w2sv.composed.playground.animatedspacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.shared.LabeledChoices
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.ResetButton
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
    SampleConfigurationCard(
        modifier = modifier,
        scrollable = scrollable,
        actions = { ResetAction(configuration, onReset) }
    ) {
        if (compact) {
            CompactControls(configuration, onConfigurationChange)
        } else {
            StandardControls(configuration, onConfigurationChange)
        }
    }
}

@Composable
private fun ResetAction(configuration: AnimatedSpacingConfiguration, onReset: () -> Unit) {
    ResetButton(
        onClick = onReset,
        enabled = configuration != AnimatedSpacingConfiguration()
    )
}

@Composable
private fun StandardControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
    SampleControlPair(
        first = {
            Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
                LayoutControls(configuration, onConfigurationChange)
                AnchorControls(configuration, onConfigurationChange)
            }
        },
        second = { AnimationControls(configuration, onConfigurationChange) }
    )
}

@Composable
private fun CompactControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)) {
        Box(Modifier.weight(1f)) { LayoutControls(configuration, onConfigurationChange) }
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
private fun LayoutControls(configuration: AnimatedSpacingConfiguration, onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit) {
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
