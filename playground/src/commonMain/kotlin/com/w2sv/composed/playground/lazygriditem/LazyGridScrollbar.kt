package com.w2sv.composed.playground.lazygriditem

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun LazyGridScrollbar(
    state: LazyGridState,
    orientation: Orientation,
    modifier: Modifier = Modifier
)
