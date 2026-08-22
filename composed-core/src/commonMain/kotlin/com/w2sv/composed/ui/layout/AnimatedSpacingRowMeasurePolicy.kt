package com.w2sv.composed.ui.layout

import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

internal class AnimatedSpacingRowMeasurePolicy(private val spacing: Dp, private val verticalAlignment: Alignment.Vertical) :
    MeasurePolicy,
    CrossAxisPosition {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult =
        measureAnimatedSpacing(
            measurables,
            constraints,
            spacing,
            AnimatedSpacingOrientation.Horizontal,
            this@AnimatedSpacingRowMeasurePolicy
        )

    override fun position(
        placeable: Placeable,
        parentData: AnimatedSpacingColumnParentData?,
        lineSpace: AlignmentLineSpace,
        crossAxisSize: Int,
        layoutDirection: LayoutDirection
    ): Int =
        when (val alignment = parentData?.crossAxisAlignment) {
            is CrossAxisAlignment.Vertical -> alignment.alignment.align(placeable.height, crossAxisSize)

            is CrossAxisAlignment.Relative -> {
                val linePosition = alignment.provider.position(placeable)
                if (linePosition == AlignmentLine.Unspecified) 0 else lineSpace.before - linePosition
            }

            else -> verticalAlignment.align(placeable.height, crossAxisSize)
        }
}
