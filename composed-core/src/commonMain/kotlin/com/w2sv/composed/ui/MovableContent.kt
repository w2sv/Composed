package com.w2sv.composed.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * Remembers [content] as [movableContentOf].
 */
@Composable
fun rememberMovableContentOf(content: @Composable () -> Unit): @Composable () -> Unit {
    val currentContent by rememberUpdatedState(content)
    return remember { movableContentOf { currentContent() } }
}

/**
 * Remembers parameterized [content] as [movableContentOf].
 */
@Composable
fun <P> rememberMovableContentOf(content: @Composable (P) -> Unit): @Composable (P) -> Unit {
    val currentContent by rememberUpdatedState(content)
    return remember { movableContentOf { parameter: P -> currentContent(parameter) } }
}

