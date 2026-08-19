package com.w2sv.composed.playground.lazygriditem

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun LazyGridScrollbar(
    state: LazyGridState,
    orientation: Orientation,
    modifier: Modifier
) {
    if (state.layoutInfo.orientation != orientation) return

    val adapter = rememberScrollbarAdapter(state)
    when (orientation) {
        Orientation.Horizontal -> HorizontalScrollbar(adapter = adapter, modifier = modifier)
        Orientation.Vertical -> VerticalScrollbar(adapter = adapter, modifier = modifier)
    }
}
