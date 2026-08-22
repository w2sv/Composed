package com.w2sv.composed.playground.lazygriditem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.w2sv.composed.animation.LazyGridItemEntranceRepeatMode
import com.w2sv.composed.playground.shared.LabeledChoices
import com.w2sv.composed.playground.shared.ParameterSlider
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.playground.shared.ResetButton
import com.w2sv.composed.playground.shared.SampleConfigurationCard
import com.w2sv.composed.playground.shared.SampleControlPair
import com.w2sv.composed.playground.shared.SampleControlSection
import com.w2sv.composed.playground.shared.toFixed
import com.w2sv.composed.ui.layout.AnimatedSpacingColumn
import kotlin.math.roundToInt

@Composable
internal fun LazyGridItemEntranceConfigurationPanel(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    horizontalLayout: Boolean,
    width: Dp,
    modifier: Modifier = Modifier
) {
    val panelExpanded = expanded || horizontalLayout
    SampleConfigurationCard(
        modifier = modifier.width(width),
        expandContent = panelExpanded,
        scrollable = horizontalLayout,
        actions = {
            ConfigurationActions(
                configuration = configuration,
                expanded = expanded,
                showExpandButton = !horizontalLayout,
                onExpandedChange = onExpandedChange,
                onReset = onReset
            )
        }
    ) {
        ConfigurationControls(configuration, onConfigurationChange, horizontalLayout)
    }
}

@Composable
private fun ConfigurationActions(
    configuration: LazyGridItemEntranceConfiguration,
    expanded: Boolean,
    showExpandButton: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    if (showExpandButton) {
        ExpandCollapseButton(expanded) { onExpandedChange(!expanded) }
    }
    ResetButton(
        onClick = onReset,
        enabled = configuration != LazyGridItemEntranceConfiguration()
    )
}

@Composable
private fun ConfigurationControls(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    horizontalLayout: Boolean
) {
    if (horizontalLayout) {
        Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
            StrategyControls(configuration, onConfigurationChange)
            TimingControls(configuration, onConfigurationChange, pairControls = false)
            AppearanceControls(configuration, onConfigurationChange, pairControls = false)
        }
    } else {
        SampleControlPair(
            first = { StrategyControls(configuration, onConfigurationChange) },
            second = {
                Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)) {
                    TimingControls(configuration, onConfigurationChange, pairControls = true)
                    AppearanceControls(configuration, onConfigurationChange, pairControls = true)
                }
            }
        )
    }
}

@Composable
private fun ExpandCollapseButton(expanded: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, contentDescription = null)
    }
}

@Composable
private fun StrategyControls(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit
) {
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
}

@Composable
private fun TimingControls(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    pairControls: Boolean
) {
    SampleControlSection(title = "Timing") {
        AnimatedSpacingColumn(spacing = PlaygroundDefaults.ControlSpacing) {
            val duration = @Composable {
                ParameterSlider(
                    label = "Duration",
                    valueLabel = "${configuration.durationMillis} ms",
                    value = configuration.durationMillis.toFloat(),
                    valueRange = 100f..1_500f,
                    onValueChange = {
                        onConfigurationChange(configuration.copy(durationMillis = it.roundToInt()))
                    }
                )
            }
            if (pairControls) {
                SampleControlPair(
                    first = duration,
                    second = { PrimaryIntervalControl(configuration, onConfigurationChange) }
                )
            } else {
                duration()
                PrimaryIntervalControl(configuration, onConfigurationChange)
            }

            AnimatedVisibility(configuration.delayMode == EntranceDelayMode.Diagonal) {
                ParameterSlider(
                    label = "Cross axis",
                    valueLabel = "${configuration.crossAxisIntervalMillis} ms",
                    value = configuration.crossAxisIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = {
                        onConfigurationChange(configuration.copy(crossAxisIntervalMillis = it.roundToInt()))
                    }
                )
            }
        }
    }
}

@Composable
private fun AppearanceControls(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit,
    pairControls: Boolean
) {
    SampleControlSection(title = "Appearance and layout") {
        val scale = @Composable {
            ParameterSlider(
                label = "Scale",
                valueLabel = configuration.initialScale.toFixed(2),
                value = configuration.initialScale,
                valueRange = 0f..2f,
                onValueChange = { onConfigurationChange(configuration.copy(initialScale = it)) }
            )
        }
        val alpha = @Composable {
            ParameterSlider(
                label = "Alpha",
                valueLabel = configuration.initialAlpha.toFixed(2),
                value = configuration.initialAlpha,
                valueRange = 0f..1f,
                onValueChange = { onConfigurationChange(configuration.copy(initialAlpha = it)) }
            )
        }
        if (pairControls) {
            SampleControlPair(first = scale, second = alpha)
        } else {
            scale()
            alpha()
        }
        ParameterSlider(
            label = "Cross-axis count",
            valueLabel = configuration.crossAxisCount.toString(),
            value = configuration.crossAxisCount.toFloat(),
            valueRange = 2f..7f,
            onValueChange = { onConfigurationChange(configuration.copy(crossAxisCount = it.roundToInt())) }
        )
    }
}

@Composable
private fun PrimaryIntervalControl(
    configuration: LazyGridItemEntranceConfiguration,
    onConfigurationChange: (LazyGridItemEntranceConfiguration) -> Unit
) {
    AnimatedSpacingColumn(spacing = 0.dp) {
        AnimatedVisibility(
            visible = configuration.delayMode == EntranceDelayMode.MainAxis ||
                configuration.delayMode == EntranceDelayMode.Diagonal
        ) {
            with(configuration) {
                ParameterSlider(
                    label = "Main axis",
                    valueLabel = "$mainAxisIntervalMillis ms",
                    value = mainAxisIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(mainAxisIntervalMillis = it.roundToInt())) }
                )
            }
        }
        AnimatedVisibility(configuration.delayMode == EntranceDelayMode.CrossAxis) {
            with(configuration) {
                ParameterSlider(
                    label = "Cross axis",
                    valueLabel = "$crossAxisIntervalMillis ms",
                    value = crossAxisIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(crossAxisIntervalMillis = it.roundToInt())) }
                )
            }
        }
        AnimatedVisibility(configuration.delayMode == EntranceDelayMode.Sequential) {
            with(configuration) {
                ParameterSlider(
                    label = "Sequential",
                    valueLabel = "$sequentialIntervalMillis ms",
                    value = sequentialIntervalMillis.toFloat(),
                    valueRange = 0f..500f,
                    onValueChange = { onConfigurationChange(copy(sequentialIntervalMillis = it.roundToInt())) }
                )
            }
        }
    }
}

private val LazyGridItemEntranceRepeatMode.label: String
    get() = when (this) {
        LazyGridItemEntranceRepeatMode.OnComposition -> "On composition"
        LazyGridItemEntranceRepeatMode.OncePerKey -> "Once per key"
    }
