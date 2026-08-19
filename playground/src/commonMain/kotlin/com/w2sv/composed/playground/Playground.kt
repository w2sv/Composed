package com.w2sv.composed.playground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.lazygriditem.LazyGridItemEntranceSample
import com.w2sv.composed.playground.shake.ShakeSample
import com.w2sv.composed.playground.shared.PlaygroundDefaults

@Composable
fun Playground(initialSample: Sample?) {
    var selectedSample by remember(initialSample) { mutableStateOf(initialSample) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    PlaygroundTopBar(
                        selectedSample = selectedSample,
                        onSampleSelected = { selectedSample = it }
                    )
                }
            ) { contentPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                    when (selectedSample) {
                        null -> SamplePicker(onSampleSelected = { selectedSample = it })
                        Sample.Shake -> ShakeSample()
                        Sample.LazyGridItemEntrance -> LazyGridItemEntranceSample()
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaygroundTopBar(selectedSample: Sample?, onSampleSelected: (Sample) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(tonalElevation = PlaygroundDefaults.TopBarElevation) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = PlaygroundDefaults.ContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Composed playground",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            Box {
                TextButton(onClick = { menuExpanded = true }) {
                    Text(selectedSample?.title ?: "Choose sample")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    Sample.entries.forEach { sample ->
                        DropdownMenuItem(
                            text = { Text(sample.title) },
                            onClick = {
                                menuExpanded = false
                                onSampleSelected(sample)
                            }
                        )
                    }
                }
            }
        }
    }
}
