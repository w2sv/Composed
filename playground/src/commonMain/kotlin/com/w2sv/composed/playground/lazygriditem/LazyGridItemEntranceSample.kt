package com.w2sv.composed.playground.lazygriditem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.w2sv.composed.animation.rememberLazyGridItemEntranceState
import com.w2sv.composed.playground.shared.PlaygroundDefaults

@Composable
fun LazyGridItemEntranceSample() {
    val defaultConfiguration = remember { LazyGridItemEntranceConfiguration() }
    var configuration by remember { mutableStateOf(defaultConfiguration) }
    var configurationExpanded by remember { mutableStateOf(true) }
    var generation by remember { mutableIntStateOf(0) }

    val gridState = rememberLazyGridState()
    val entranceState = rememberLazyGridItemEntranceState(gridState)

    fun replay() {
        entranceState.reset()
        generation++
    }

    fun updateConfiguration(nextConfiguration: LazyGridItemEntranceConfiguration) {
        if (nextConfiguration.orientation != configuration.orientation) {
            entranceState.reset()
            generation++
        }
        configuration = nextConfiguration
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ReplayActionsCard(
                onReplay = { replay() },
                onReset = {
                    configuration = defaultConfiguration
                    replay()
                }
            )
        }
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            key(generation) {
                LazyGridItemEntranceGrid(
                    configuration = configuration,
                    onConfigurationChange = ::updateConfiguration,
                    configurationExpanded = configurationExpanded,
                    onConfigurationExpandedChange = { configurationExpanded = it },
                    gridState = gridState,
                    entranceState = entranceState,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ReplayActionsCard(
    onReplay: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(PlaygroundDefaults.CompactSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedButton(onClick = onReset) { Text("Reset") }
            Button(onClick = onReplay) { Text("Replay") }
        }
    }
}
