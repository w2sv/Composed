package com.w2sv.composed.core

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * @return rememberSavable state saver for [Color].
 */
fun colorSaver() =
    Saver<Color, Int>(save = { it.toArgb() }, restore = { Color(it) })

/**
 * @return rememberSavable state saver for an optional [Color].
 */
fun nullableColorSaver() =
    nullableListSaver<Color, Float>(
        saveNonNull = {
            listOf(it.red, it.green, it.blue, it.alpha)
        },
        restoreNonNull = {
            Color(red = it[0], green = it[1], blue = it[2], alpha = it[3])
        }
    )

/**
 * [listSaver] for an optional object of type [Original], that enables you to omit the handling of the case in which the state value == null. Only the saving and restoring of the non-null instance needs to be handled.
 */
fun <Original, Savable> nullableListSaver(
    saveNonNull: SaverScope.(value: Original) -> List<Savable>,
    restoreNonNull: (list: List<Savable>) -> Original?
): Saver<Original?, Any> =
    listSaver(
        save = { original ->
            original?.let { saveNonNull(it) } ?: emptyList()
        },
        restore = {
            if (it.isEmpty()) {
                null
            } else {
                restoreNonNull(it)
            }
        }
    )

/**
 * [mapSaver] for an optional object of type [T], that enables you to omit the handling of the case in which the state value == null. Only the saving and restoring of the non-null instance needs to be handled.
 */
fun <T> nullableMapSaver(saveNonNull: SaverScope.(value: T) -> Map<String, Any?>, restoreNonNull: (Map<String, Any?>) -> T): Saver<T, Any> =
    mapSaver(
        save = { original ->
            original?.let { saveNonNull(it) } ?: emptyMap()
        },
        restore = {
            if (it.isEmpty()) {
                null
            } else {
                restoreNonNull(it)
            }
        }
    )
