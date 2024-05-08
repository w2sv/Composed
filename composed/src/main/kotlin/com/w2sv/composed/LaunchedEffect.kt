package com.w2sv.composed

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest

/**
 * Collect from [flow] and emit values into [collector], whilst relaunching the collection upon [flow], [key1] or [key2] changing.
 */
@SuppressLint("ComposeParameterOrder")
@Composable
fun <T> CollectFromFlow(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    collector: FlowCollector<T>
) {
    LaunchedEffect(flow, key1, key2) {
        flow.collect(collector)
    }
}

/**
 * Collect latest from [flow] with given [action], whilst relaunching the collection upon [flow], [key1] or [key2] changing.
 * @see Flow.collectLatest
 */
@Composable
fun <T> CollectLatestFromFlow(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    action: suspend (value: T) -> Unit
) {
    LaunchedEffect(flow, key1, key2) {
        flow.collectLatest(action)
    }
}

@Composable
fun <T> OnChange(
    value: T,
    key1: Any? = null,
    key2: Any? = null,
    callback: suspend (T) -> Unit
) {
    val updatedCallback by rememberUpdatedState(newValue = callback)

    LaunchedEffect(value, key1, key2) {
        updatedCallback(value)
    }
}