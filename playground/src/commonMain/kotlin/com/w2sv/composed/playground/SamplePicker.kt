package com.w2sv.composed.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.shared.PlaygroundDefaults

@Composable
internal fun SamplePicker(onSampleSelected: (Sample) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(PlaygroundDefaults.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = PlaygroundDefaults.SamplePickerWidth),
            verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.ControlSpacing)
        ) {
            Text("Choose a sample", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Select an API to explore. You can switch samples from the top bar at any time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Sample.entries.forEach { sample ->
                ElevatedCard(
                    onClick = { onSampleSelected(sample) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(PlaygroundDefaults.ContentPadding),
                        verticalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing)
                    ) {
                        Text(sample.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = sample.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
