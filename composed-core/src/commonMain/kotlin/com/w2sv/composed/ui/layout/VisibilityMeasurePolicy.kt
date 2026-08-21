package com.w2sv.composed.ui.layout

import androidx.compose.runtime.State
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.roundToInt

internal class VisibilityMeasurePolicy(private val presence: State<Float>, private val fillWeightedSpace: Boolean) : MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val fillHeight =
            fillWeightedSpace &&
                constraints.maxHeight != Constraints.Infinity

        val childConstraints = constraints.copy(
            minHeight = if (fillHeight) constraints.maxHeight else 0
        )

        val placeables = Array<Placeable?>(measurables.size) { index ->
            measurables[index].measure(childConstraints)
        }

        var width = 0
        var fullHeight = 0

        placeables.forEach { placeable ->
            if (placeable != null) {
                width = maxOf(width, placeable.width)
                fullHeight = maxOf(fullHeight, placeable.height)
            }
        }

        val animatedHeight = (
            fullHeight *
                presence.value.coerceIn(0f, 1f)
            ).roundToInt()

        return layout(
            width = constraints.constrainWidth(width),
            height = constraints.constrainHeight(animatedHeight)
        ) {
            placeables.forEach {
                it?.placeRelative(0, 0)
            }
        }
    }
}
