package com.w2sv.composed.playground.shared

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.oikvpqya.compose.fastscroller.HorizontalScrollbar
import io.github.oikvpqya.compose.fastscroller.ScrollbarStyle
import io.github.oikvpqya.compose.fastscroller.ThumbStyle
import io.github.oikvpqya.compose.fastscroller.TrackStyle
import io.github.oikvpqya.compose.fastscroller.VerticalScrollbar
import io.github.oikvpqya.compose.fastscroller.rememberScrollbarAdapter

@Composable
internal fun PlaygroundScrollbar(
    state: LazyGridState,
    orientation: Orientation,
    modifier: Modifier = Modifier
) {
    val adapter = rememberScrollbarAdapter(state)
    val style = playgroundScrollbarStyle()
    when (orientation) {
        Orientation.Horizontal -> HorizontalScrollbar(adapter = adapter, style = style, modifier = modifier)
        Orientation.Vertical -> VerticalScrollbar(adapter = adapter, style = style, modifier = modifier)
    }
}

@Composable
internal fun PlaygroundVerticalScrollbar(state: ScrollState, modifier: Modifier = Modifier) {
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(state),
        style = playgroundScrollbarStyle(),
        modifier = modifier
    )
}

@Composable
private fun playgroundScrollbarStyle(): ScrollbarStyle {
    val thumbColor = MaterialTheme.colorScheme.onSurface
    val shape = RoundedCornerShape(PlaygroundDefaults.ScrollbarThickness / 2)

    return ScrollbarStyle(
        minimalHeight = 52.dp,
        thickness = PlaygroundDefaults.ScrollbarThickness,
        hoverDurationMillis = 300,
        thumbStyle = ThumbStyle(
            shape = shape,
            unhoverColor = thumbColor.copy(alpha = 0.12f),
            hoverColor = thumbColor.copy(alpha = 0.50f)
        ),
        trackStyle = TrackStyle(
            shape = shape,
            unhoverColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            hoverColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
        )
    )
}
