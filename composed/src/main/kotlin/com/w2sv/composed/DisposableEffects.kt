package com.w2sv.composed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Run the given [callback] inside of a [LifecycleEventObserver] launched as a [DisposableEffect], when the [lifecycleOwner] reaches the given [lifecycleEvent] and clean up the observer when [lifecycleOwner], [key1] or [key2] changes or if [OnLifecycleEvent] leaves the composition.
 * @see DisposableEffect
 */
@Composable
fun OnLifecycleEvent(
    callback: () -> Unit,
    lifecycleEvent: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    key1: Any? = null,
    key2: Any? = null
) {
    DisposableEffect(lifecycleOwner, key1, key2) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                callback()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnRemoveFromComposition(callback: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            callback()
        }
    }
}