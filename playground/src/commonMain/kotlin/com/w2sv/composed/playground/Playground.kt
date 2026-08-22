package com.w2sv.composed.playground

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.w2sv.composed.playground.animatedspacing.AnimatedSpacingSample
import com.w2sv.composed.playground.lazygriditem.LazyGridItemEntranceSample
import com.w2sv.composed.playground.shake.ShakeSample

@Composable
fun Playground(initialSample: Sample?) {
    var selectedSample by remember(initialSample) { mutableStateOf(initialSample) }
    val systemInDarkTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(systemInDarkTheme) }
    val colorScheme = if (darkTheme) darkColorScheme() else expressiveLightColorScheme()

    MaterialExpressiveTheme(colorScheme = colorScheme) {
        Scaffold(
            topBar = {
                PlaygroundTopBar(
                    selectedSample = selectedSample,
                    onSampleSelected = { selectedSample = it },
                    darkTheme = darkTheme,
                    onDarkThemeChanged = { darkTheme = it }
                )
            }
        ) { contentPadding ->
            AnimatedContent(
                selectedSample,
                modifier = Modifier.fillMaxSize().padding(contentPadding)
            ) { sample ->
                when (sample) {
                    null -> SamplePicker(onSampleSelected = { selectedSample = it })
                    Sample.AnimatedSpacing -> AnimatedSpacingSample()
                    Sample.Shake -> ShakeSample()
                    Sample.LazyGridItemEntrance -> LazyGridItemEntranceSample()
                }
            }
        }
    }
}

@Composable
private fun PlaygroundTopBar(
    selectedSample: Sample?,
    onSampleSelected: (Sample) -> Unit,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = selectedSample?.title ?: "Composed playground") },
        navigationIcon = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    Sample.entries.forEach { sample ->
                        DropdownMenuItem(
                            text = { Text(sample.title) },
                            enabled = sample != selectedSample,
                            onClick = {
                                menuExpanded = false
                                onSampleSelected(sample)
                            }
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = { onDarkThemeChanged(!darkTheme) }) {
                Crossfade(darkTheme) { darkTheme ->
                    Icon(
                        imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (darkTheme) "Switch to light mode" else "Switch to dark mode"
                    )
                }
            }
        }
    )
}
