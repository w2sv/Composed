package com.w2sv.composeutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Run the given [callback] inside of a [LifecycleEventObserver] launched as a [DisposableEffect], when the [lifecycleOwner] reaches the given [lifecycleEvent] and clean up the observer when [lifecycleOwner], [key1] or [key2] changes or if [DoOnLifecycleEvent] leaves the composition.
 * @see DisposableEffect
 */
@Composable
fun DoOnLifecycleEvent(
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