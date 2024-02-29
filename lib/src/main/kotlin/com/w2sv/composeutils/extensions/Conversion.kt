package com.w2sv.composeutils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
@ReadOnlyComposable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
@ReadOnlyComposable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
@ReadOnlyComposable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }