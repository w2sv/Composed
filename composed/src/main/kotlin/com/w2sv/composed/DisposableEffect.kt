package com.w2sv.composed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Run the given [callback] inside of a [LifecycleEventObserver] launched as a [DisposableEffect], when the [lifecycleOwner] reaches the given [lifecycleEvent] and clean up the observer when [lifecycleOwner], [key1] or [key2] changes or if [OnLifecycleEvent] leaves the composition.
 * @see DisposableEffect
 */
@Composable
fun OnLifecycleEvent(
    lifecycleEvent: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    key1: Any? = null,
    key2: Any? = null,
    callback: () -> Unit
) {
    val currentCallback by rememberUpdatedState(newValue = callback)
    val currentLifecycleEvent by rememberUpdatedState(newValue = lifecycleEvent)

    DisposableEffect(lifecycleOwner, key1, key2) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == currentLifecycleEvent) {
                currentCallback()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnDispose(callback: () -> Unit) {
    val currentCallback by rememberUpdatedState(newValue = callback)

    DisposableEffect(Unit) {
        onDispose {
            currentCallback()
        }
    }
}