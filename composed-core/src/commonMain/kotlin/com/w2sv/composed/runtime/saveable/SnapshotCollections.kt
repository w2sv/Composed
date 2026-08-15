package com.w2sv.composed.runtime.saveable

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import com.w2sv.composed.runtime.toMutableStateSet

/**
 * [Saver] for a [SnapshotStateList] that saves its elements and restores them
 * into a new snapshot-observable mutable list.
 */
fun <T> snapshotStateListSaver() =
    listSaver<SnapshotStateList<T>, T>(
        save = { it.toList() },
        restore = { it.toMutableStateList() }
    )

/**
 * [Saver] for a [SnapshotStateMap] that saves its entries and restores them
 * into a new snapshot-observable mutable map.
 */
fun <K, V> snapshotStateMapSaver() =
    listSaver<SnapshotStateMap<K, V>, Pair<K, V>>(
        save = { it.toList() },
        restore = { it.toMutableStateMap() }
    )

/**
 * [Saver] for a [SnapshotStateSet] that saves its elements and restores them
 * into a new snapshot-observable mutable set.
 */
fun <T> snapshotStateSetSaver() =
    listSaver<SnapshotStateSet<T>, T>(
        save = { it.toList() },
        restore = { it.toMutableStateSet() }
    )
