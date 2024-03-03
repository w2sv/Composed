package com.w2sv.composeutils.extensions

import androidx.annotation.FloatRange
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

fun DrawerState.visibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float): State<Float> {
    check(maxWidthPx >= -currentOffset)
    return derivedStateOf { currentOffset / maxWidthPx + 1 }  // currentOffset = -MAX when completely hidden, 0 when completely visible
}

@Composable
fun DrawerState.rememberVisibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float = DrawerDefaults.MaximumDrawerWidth.toPx()): State<Float> =
    remember {
        visibilityPercentage(maxWidthPx)
    }