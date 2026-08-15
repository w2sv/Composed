package com.w2sv.composed.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Executes [callback] when the current composition leaves the composition.
 *
 * This is a convenience wrapper around `DisposableEffect(Unit)` that ensures
 * the latest provided [callback] is invoked during disposal.
 */
@Composable
fun OnDispose(callback: () -> Unit) {
    val currentCallback by rememberUpdatedState(newValue = callback)

    DisposableEffect(Unit) {
        onDispose {
            currentCallback()
        }
    }
}
