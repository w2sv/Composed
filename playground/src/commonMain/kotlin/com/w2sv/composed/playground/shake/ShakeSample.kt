package com.w2sv.composed.playground.shake

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun ShakeSample() {
    var configuration by remember { mutableStateOf(ShakeConfiguration()) }

    ShakeLayout(
        configuration = configuration,
        onConfigurationChange = { configuration = it },
        onReset = { configuration = ShakeConfiguration() },
        modifier = Modifier.fillMaxSize()
    )
}
