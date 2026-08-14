package com.w2sv.composed.material3.extensions

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.w2sv.composed.core.extensions.toPx

/**
 * @return Float [State] whose value ranges from 0.0 (drawer fully closed) to 1.0 (drawer fully open).
 * @param maxWidthPx The max drawer sheet width.
 */
fun DrawerState.visibilityProgress(maxWidthPx: Float): State<Float> {
    require(maxWidthPx != 0f) { "maxWidthPx can not be 0f" }
    return derivedStateOf {
        // currentOffset ∈ [-maxWidthPx (fully hidden), 0 (fully visible)]
        ((currentOffset + maxWidthPx) / maxWidthPx).coerceIn(0f, 1f)
    }
}

/**
 * @return the remembered drawer visibility percentage ranging from 0 to 1.
 * @param maxWidth The max drawer sheet width. Will equal [DrawerDefaults.MaximumDrawerWidth] by default.
 */
@Composable
fun DrawerState.rememberVisibilityProgress(maxWidth: Dp = DrawerDefaults.MaximumDrawerWidth): State<Float> {
    val maxWidthPx = maxWidth.toPx()
    return remember(maxWidthPx) { visibilityProgress(maxWidthPx) }
}
