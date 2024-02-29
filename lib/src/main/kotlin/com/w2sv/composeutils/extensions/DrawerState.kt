package com.w2sv.composeutils.extensions

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf

fun DrawerState.visibilityPercentage(maxWidthPx: Float): State<Float> =
    derivedStateOf { currentOffset / maxWidthPx + 1 }  // currentOffset = -MAX when completely hidden, 0 when completely visible