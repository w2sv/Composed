package com.w2sv.composed.playground.lazygriditem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.w2sv.composed.animation.rememberLazyGridItemEntranceState

@Composable
fun LazyGridItemEntranceSample() {
    val defaultConfiguration = remember { LazyGridItemEntranceConfiguration() }
    var configuration by remember { mutableStateOf(defaultConfiguration) }
    var configurationExpanded by remember { mutableStateOf(true) }
    var generation by remember { mutableIntStateOf(0) }

    val gridState = key(configuration.orientation) { rememberLazyGridState() }
    val entranceState = rememberLazyGridItemEntranceState(gridState)

    fun replay() {
        entranceState.reset()
        generation++
    }

    fun updateConfiguration(nextConfiguration: LazyGridItemEntranceConfiguration) {
        if (nextConfiguration.orientation != configuration.orientation) {
            entranceState.reset()
        }
        configuration = nextConfiguration
    }

    fun reset() {
        val orientationChanges = configuration.orientation != defaultConfiguration.orientation
        configuration = defaultConfiguration
        entranceState.reset()
        if (!orientationChanges) {
            generation++
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { replay() }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Replay, null)
                    Text("Replay")
                }
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
            key(generation) {
                LazyGridItemEntranceGrid(
                    configuration = configuration,
                    onConfigurationChange = ::updateConfiguration,
                    configurationExpanded = configurationExpanded,
                    onConfigurationExpandedChange = { configurationExpanded = it },
                    onReset = ::reset,
                    gridState = gridState,
                    entranceState = entranceState,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
