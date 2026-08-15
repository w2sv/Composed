package com.w2sv.composed.runtime.saveable

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver

/**
 * [listSaver] for a nullable value.
 *
 * `null` is represented by an empty list, so [saveNonNull] must return a
 * non-empty list for every non-null value.
 */
fun <Original, Savable> nullableListSaver(
    saveNonNull: SaverScope.(Original) -> List<Savable>,
    restoreNonNull: (List<Savable>) -> Original?
): Saver<Original?, Any> =
    listSaver(
        save = { value -> value?.let { saveNonNull(it) } ?: emptyList() },
        restore = { saved -> if (saved.isEmpty()) null else restoreNonNull(saved) }
    )

/**
 * [mapSaver] for a nullable value.
 *
 * `null` is represented by an empty map, so [saveNonNull] must return a
 * non-empty map for every non-null value.
 */
fun <Original> nullableMapSaver(
    saveNonNull: SaverScope.(Original) -> Map<String, Any?>,
    restoreNonNull: (Map<String, Any?>) -> Original?
): Saver<Original?, Any> =
    mapSaver(
        save = { value -> value?.let { saveNonNull(it) } ?: emptyMap() },
        restore = { saved -> if (saved.isEmpty()) null else restoreNonNull(saved) }
    )
