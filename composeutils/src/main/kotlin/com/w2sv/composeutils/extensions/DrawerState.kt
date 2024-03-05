package com.w2sv.composeutils.extensions

import androidx.annotation.FloatRange
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * @return Float [State] whose value ranges from 0.0 (drawer fully closed) to 1.0 (drawer fully open).
 * @param maxWidthPx The with in pixels upon the drawer being fully expanded. If unmodified, this will equal [DrawerDefaults.MaximumDrawerWidth].
 */
fun DrawerState.visibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float): State<Float> {
    return derivedStateOf { currentOffset / maxWidthPx + 1 }  // currentOffset = -MAX when completely hidden, 0 when completely visible
}

/**
 * @see visibilityPercentage
 */
@Composable
fun DrawerState.rememberVisibilityPercentage(@FloatRange(from = 0.0) maxWidthPx: Float = DrawerDefaults.MaximumDrawerWidth.toPx()): State<Float> =
    remember {
        visibilityPercentage(maxWidthPx)
    }