package com.w2sv.composed.playground.animatedspacing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.w2sv.composed.playground.shared.PlaygroundDefaults
import com.w2sv.composed.ui.layout.ExperimentalAnimatedSpacingApi

@Composable
@OptIn(ExperimentalAnimatedSpacingApi::class)
fun AnimatedSpacingSample() {
    val defaultConfiguration = remember { AnimatedSpacingConfiguration() }
    var configuration by remember { mutableStateOf(defaultConfiguration) }
    var visibility by remember { mutableStateOf(AnimatedSpacingVisibility()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedSpacingActionsCard(
                allVisible = visibility.allVisible,
                onToggleAll = {
                    visibility = if (visibility.allVisible) AnimatedSpacingVisibility.none() else AnimatedSpacingVisibility()
                },
                onReset = {
                    configuration = defaultConfiguration
                    visibility = AnimatedSpacingVisibility()
                }
            )
        }
    ) { contentPadding ->
        Box(Modifier.fillMaxSize().padding(contentPadding)) {
            AnimatedSpacingLayout(
                configuration = configuration,
                onConfigurationChange = { configuration = it },
                visibility = visibility,
                onVisibilityChange = { visibility = it },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun AnimatedSpacingActionsCard(
    allVisible: Boolean,
    onToggleAll: () -> Unit,
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
            Button(onClick = onToggleAll) { Text(if (allVisible) "Hide all" else "Show all") }
        }
    }
}
