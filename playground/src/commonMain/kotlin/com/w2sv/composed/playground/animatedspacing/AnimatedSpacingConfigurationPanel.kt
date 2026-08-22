package com.w2sv.composed.playground.animatedspacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.shared.LabeledChoices
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.SampleConfigurationCard
import com.w2sv.composed.playground.shared.SampleControlPair
import com.w2sv.composed.playground.shared.SampleControlSection
import kotlin.math.roundToInt

@Composable
internal fun AnimatedSpacingConfigurationPanel(
    configuration: AnimatedSpacingConfiguration,
    onConfigurationChange: (AnimatedSpacingConfiguration) -> Unit,
    modifier: Modifier = Modifier
) {
    SampleConfigurationCard(
        title = "Animated spacing layouts",
        description = "Toggle items below and compare fixed sizing with animated weight redistribution in a row or column.",
        modifier = modifier
    ) {
        SampleControlPair(
            first = {
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
                }
            },
            second = {
                Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
                    SampleControlSection("Animation") {
                        LabeledChoices(
                            label = "Fade",
                            values = listOf(true, false),
                            selected = configuration.fade,
                            onSelected = { onConfigurationChange(configuration.copy(fade = it)) },
                            valueLabel = { if (it) "On" else "Off" }
                        )
                        ParameterSlider(
                            label = "Duration",
                            valueLabel = "${configuration.durationMillis} ms",
                            value = configuration.durationMillis.toFloat(),
                            valueRange = 100f..1_500f,
                            onValueChange = { onConfigurationChange(configuration.copy(durationMillis = it.roundToInt())) }
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
            }
        )
    }
}
