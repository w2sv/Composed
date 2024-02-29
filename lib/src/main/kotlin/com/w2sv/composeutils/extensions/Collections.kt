package com.w2sv.composeutils.extensions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap

fun <K, V> Map<K, V>.toMutableStateMap(): SnapshotStateMap<K, V> =
    mutableStateMapOf<K, V>()
        .apply { putAll(this@toMutableStateMap) }

fun <T> Iterable<T>.toMutableStateList(): SnapshotStateList<T> =
    mutableStateListOf<T>().apply {
        addAll(this@toMutableStateList)
    }