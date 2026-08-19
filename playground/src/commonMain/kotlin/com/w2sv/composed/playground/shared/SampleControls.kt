package com.w2sv.composed.playground.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SampleConfigurationCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    headerAction: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = contentModifier.padding(PlaygroundDefaults.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.SectionSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)
                ) {
                    Text(text = title, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                headerAction?.invoke()
            }

            content()
        }
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
internal fun <T> LabeledChoices(
    label: String,
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    valueLabel: (T) -> String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SampleChoiceChips(
            values = values,
            selected = selected,
            onSelected = onSelected,
            label = valueLabel
        )
    }
}

@Composable
internal fun <T> SampleChoiceChips(
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() }
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing),
        verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)
    ) {
        values.forEach { value ->
            FilterChip(
                selected = value == selected,
                onClick = { onSelected(value) },
                label = { Text(label(value)) }
            )
        }
    }
}
