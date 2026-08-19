package com.w2sv.composed.playground.lazygriditem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.w2sv.composed.animation.LazyGridItemEntranceRepeatMode
import com.w2sv.composed.playground.shared.LabeledChoices
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.SampleConfigurationCard
import com.w2sv.composed.playground.shared.SampleControlPair
import com.w2sv.composed.playground.shared.SampleControlSection
import com.w2sv.composed.ui.thenIf
import kotlin.math.roundToInt

@Composable
internal fun LazyGridItemEntranceConfigurationPanel(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    horizontalLayout: Boolean,
    modifier: Modifier = Modifier
) {
    val horizontalWidth by animateDpAsState(
        targetValue = if (expanded) {
            LazyGridItemEntranceDimens.HorizontalConfigurationWidth
        } else {
            LazyGridItemEntranceDimens.HorizontalCollapsedConfigurationWidth
        }
    )

    SampleConfigurationCard(
        title = "Lazy grid entrances",
        description = "Configure the stagger, then replay it across the visible grid items.",
        modifier = modifier.thenIf(horizontalLayout) { width(horizontalWidth) },
        headerAction = {
            TextButton(onClick = { onExpandedChange(!expanded) }) {
                Text(if (expanded) "Collapse settings" else "Expand settings")
            }
        }
    ) {
        AnimatedVisibility(
            expanded,
            enter = fadeIn() + if (horizontalLayout) expandHorizontally() else expandVertically(),
            exit = if (horizontalLayout) shrinkHorizontally() else shrinkVertically() + fadeOut()
        ) {
            SampleControlPair(
                first = {
                    SampleControlSection(title = "Strategy") {
                        LabeledChoices(
                            label = "Orientation",
                            values = GridOrientation.entries,
                            selected = configuration.orientation,
                            onSelected = { onConfigurationChange(configuration.copy(orientation = it)) },
                            valueLabel = GridOrientation::label
                        )

                        LabeledChoices(
                            label = "Repeat",
                            values = LazyGridItemEntranceRepeatMode.entries,
                            selected = configuration.repeatMode,
                            onSelected = { onConfigurationChange(configuration.copy(repeatMode = it)) },
                            valueLabel = LazyGridItemEntranceRepeatMode::label
                        )

                        LabeledChoices(
                            label = "Delay",
                            values = EntranceDelayMode.entries,
                            selected = configuration.delayMode,
                            onSelected = { onConfigurationChange(configuration.copy(delayMode = it)) },
                            valueLabel = EntranceDelayMode::label
                        )
                    }
                },
                second = {
                    Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
                        SampleControlSection(title = "Timing") {
                            SampleControlPair(
                                first = {
                                    ParameterSlider(
                                        label = "Duration",
                                        valueLabel = "${configuration.durationMillis} ms",
                                        value = configuration.durationMillis.toFloat(),
                                        valueRange = 100f..1_500f,
                                        onValueChange = {
                                            onConfigurationChange(
                                                configuration.copy(durationMillis = it.roundToInt())
                                            )
                                        }
                                    )
                                },
                                second = configuration.primaryIntervalControl(onConfigurationChange)
                            )

                            AnimatedVisibility(configuration.delayMode == EntranceDelayMode.Diagonal) {
                                ParameterSlider(
                                    label = "Cross axis",
                                    valueLabel = "${configuration.crossAxisIntervalMillis} ms",
                                    value = configuration.crossAxisIntervalMillis.toFloat(),
                                    valueRange = 0f..500f,
                                    onValueChange = {
                                        onConfigurationChange(
                                            configuration.copy(crossAxisIntervalMillis = it.roundToInt())
                                        )
                                    }
                                )
                            }
                        }

                        SampleControlSection(title = "Appearance and layout") {
                            SampleControlPair(
                                first = {
                                    ParameterSlider(
                                        label = "Scale",
                                        valueLabel = "%.2f".format(configuration.initialScale),
                                        value = configuration.initialScale,
                                        valueRange = 0f..2f,
                                        onValueChange = {
                                            onConfigurationChange(configuration.copy(initialScale = it))
                                        }
                                    )
                                },
                                second = {
                                    ParameterSlider(
                                        label = "Alpha",
                                        valueLabel = "%.2f".format(configuration.initialAlpha),
                                        value = configuration.initialAlpha,
                                        valueRange = 0f..1f,
                                        onValueChange = {
                                            onConfigurationChange(configuration.copy(initialAlpha = it))
                                        }
                                    )
                                }
                            )

                            ParameterSlider(
                                label = "Cross-axis count",
                                valueLabel = configuration.crossAxisCount.toString(),
                                value = configuration.crossAxisCount.toFloat(),
                                valueRange = 2f..7f,
                                onValueChange = { onConfigurationChange(configuration.copy(crossAxisCount = it.roundToInt())) }
                            )
                        }
                    }
                }
            )
        }
    }
}

private fun LazyGridItemEntranceConfiguration.primaryIntervalControl(
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit
): (@Composable () -> Unit)? =
    when (delayMode) {
        EntranceDelayMode.None -> null
        EntranceDelayMode.MainAxis,
        EntranceDelayMode.Diagonal -> (
            {
                ParameterSlider(
                    label = "Main axis",
                    valueLabel = "$mainAxisIntervalMillis ms",
                    value = mainAxisIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(mainAxisIntervalMillis = it.roundToInt())) }
                )
            }
            )

        EntranceDelayMode.CrossAxis -> (
            {
                ParameterSlider(
                    label = "Cross axis",
                    valueLabel = "$crossAxisIntervalMillis ms",
                    value = crossAxisIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(crossAxisIntervalMillis = it.roundToInt())) }
                )
            }
            )

        EntranceDelayMode.Sequential -> (
            {
                ParameterSlider(
                    label = "Sequential",
                    valueLabel = "$sequentialIntervalMillis ms",
                    value = sequentialIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(sequentialIntervalMillis = it.roundToInt())) }
                )
            }
            )
    }

private val LazyGridItemEntranceRepeatMode.label: String
    get() = when (this) {
        LazyGridItemEntranceRepeatMode.OnComposition -> "On composition"
        LazyGridItemEntranceRepeatMode.OncePerKey -> "Once per key"
    }
