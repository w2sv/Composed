package com.w2sv.composed.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Converts this [Dp] value to raw pixels using the current [LocalDensity].
 */
@Composable
@ReadOnlyComposable
fun Dp.toPx() =
    with(LocalDensity.current) { this@toPx.toPx() }

/**
 * Converts this pixel value to [Dp] using the current [LocalDensity].
 */
@Composable
@ReadOnlyComposable
fun Int.toDp() =
    with(LocalDensity.current) { this@toDp.toDp() }

/**
 * Converts this pixel value to [Dp] using the current [LocalDensity].
 */
@Composable
@ReadOnlyComposable
fun Float.toDp() =
    with(LocalDensity.current) { this@toDp.toDp() }
