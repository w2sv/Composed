package com.w2sv.composed.runtime

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateSet

/**
 * Converts this [Map] to a [SnapshotStateMap].
 */
fun <K, V> Map<K, V>.toMutableStateMap(): SnapshotStateMap<K, V> =
    mutableStateMapOf<K, V>().also { it.putAll(this) }

/**
 * Converts this [Collection] to a [SnapshotStateSet].
 */
fun <T> Collection<T>.toMutableStateSet(): SnapshotStateSet<T> =
    mutableStateSetOf<T>().also { it.addAll(this) }
