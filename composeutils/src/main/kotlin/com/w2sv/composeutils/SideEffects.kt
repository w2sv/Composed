package com.w2sv.composeutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

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

        with(lifecycleOwner.lifecycle) {
            addObserver(observer)

            onDispose {
                removeObserver(observer)
            }
        }
    }
}