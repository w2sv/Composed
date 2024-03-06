package com.w2sv.composed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest

/**
 * Collect from [flow] and emit values into [collector], whilst relaunching the collection upon [flow], [key1] or [key2] changing.
 */
@Composable
fun <T> CollectFromFlow(
    flow: Flow<T>,
    collector: FlowCollector<T>,
    key1: Any? = null,
    key2: Any? = null
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
    action: suspend (value: T) -> Unit,
    key1: Any? = null,
    key2: Any? = null
) {
    LaunchedEffect(flow, key1, key2) {
        flow.collectLatest(action)
    }
}