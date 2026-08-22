package com.w2sv.composed.playground.animatedspacing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AnimatedSpacingSample() {
    val defaultConfiguration = remember { AnimatedSpacingConfiguration() }
    var configuration by remember { mutableStateOf(defaultConfiguration) }
    var visibility by remember { mutableStateOf(AnimatedSpacingVisibility()) }

    Box(Modifier.fillMaxSize()) {
        AnimatedSpacingLayout(
            configuration = configuration,
            onConfigurationChange = { configuration = it },
            visibility = visibility,
            onVisibilityChange = { visibility = it },
            onReset = { configuration = defaultConfiguration },
            modifier = Modifier.fillMaxSize()
        )
    }
}
