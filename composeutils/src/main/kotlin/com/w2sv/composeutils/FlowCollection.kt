package com.w2sv.composeutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest

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