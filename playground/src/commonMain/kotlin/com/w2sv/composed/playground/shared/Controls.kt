package com.w2sv.composed.playground.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.w2sv.composed.ui.layout.AnimatedSpacingColumn

@Composable
internal fun SampleConfigurationCard(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    expandContent: Boolean = true,
    scrollable: Boolean = false,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    ElevatedCard(modifier = modifier) {
        Box {
            AnimatedSpacingColumn(
                spacing = PlaygroundDefaults.SectionSpacing,
                modifier = if (scrollable) Modifier.verticalScroll(scrollState) else Modifier
            ) {
                ConfigurationCardTopBar(actions = actions)

                AnimatedVisibility(visible = expandContent) {
                    ConfigurationCardContent(content)
                }
            }
            if (scrollable) {
                PlaygroundVerticalScrollbar(
                    state = scrollState,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(vertical = PlaygroundDefaults.ScrollbarEdgePadding)
                        .width(PlaygroundDefaults.ScrollbarThickness)
                )
            }
        }
    }
}

@Composable
private fun ConfigurationCardTopBar(actions: @Composable RowScope.() -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(horizontal = PlaygroundDefaults.ContentPadding, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        actions()
    }
}

@Composable
private fun ConfigurationCardContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = PlaygroundDefaults.ContentPadding)
            .padding(bottom = PlaygroundDefaults.SectionSpacing)
    ) {
        content()
    }
}

@Composable
internal fun ResetButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(Icons.Rounded.SettingsBackupRestore, null)
    }
}

@Composable
internal fun SampleControlPair(
    first: @Composable () -> Unit,
    second: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (second != null && maxWidth >= PlaygroundDefaults.TwoColumnBreakpoint) {
            Row(horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)) {
                Box(modifier = Modifier.weight(1f)) { first() }
                Box(modifier = Modifier.weight(1f)) { second() }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)) {
                first()
                second?.invoke()
            }
        }
    }
}

@Composable
internal fun SampleControlSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        content()
    }
}

@Composable
internal fun ParameterSlider(
    label: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(PlaygroundDefaults.SliderLabelWidth),
            style = MaterialTheme.typography.controlLabel
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valueLabel,
            modifier = Modifier.width(PlaygroundDefaults.SliderValueWidth),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun <T> LabeledChoices(
    label: String,
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    valueLabel: (T) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.controlLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SampleConnectedButtonGroup(
            values = values,
            selected = selected,
            onSelected = onSelected,
            label = valueLabel
        )
    }
}

@Composable
private fun <T> SampleConnectedButtonGroup(
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() }
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        values.forEachIndexed { index, value ->
            ToggleButton(
                checked = value == selected,
                onCheckedChange = { onSelected(value) },
                modifier = Modifier.semantics { role = Role.RadioButton },
                shapes = connectedButtonShapes(index, values.lastIndex)
            ) {
                Text(label(value))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun connectedButtonShapes(index: Int, lastIndex: Int): ToggleButtonShapes =
    when (index) {
        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
        lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
    }
