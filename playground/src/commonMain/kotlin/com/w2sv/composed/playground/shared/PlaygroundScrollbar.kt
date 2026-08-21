package com.w2sv.composed.playground.shared

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlaygroundScrollbar(
    state: LazyGridState,
    orientation: Orientation,
    modifier: Modifier = Modifier
)

@Composable
internal expect fun PlaygroundVerticalScrollbar(state: ScrollState, modifier: Modifier = Modifier)
