package com.w2sv.composed.ui.layout.animatedspacing

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

internal class AnimatedSpacingColumnMeasurePolicy(private val spacing: Dp, private val horizontalAlignment: Alignment.Horizontal) :
    MeasurePolicy,
    CrossAxisPosition {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult =
        measureAnimatedSpacing(
            measurables,
            constraints,
            spacing,
            AnimatedSpacingOrientation.Vertical,
            this@AnimatedSpacingColumnMeasurePolicy
        )

    override fun position(
        placeable: Placeable,
        parentData: AnimatedSpacingParentData?,
        lineSpace: AlignmentLineSpace,
        crossAxisSize: Int,
        layoutDirection: LayoutDirection
    ): Int =
        when (val alignment = parentData?.crossAxisAlignment) {
            is CrossAxisAlignment.Horizontal -> alignment.alignment.align(placeable.width, crossAxisSize, layoutDirection)

            is CrossAxisAlignment.Relative -> {
                val linePosition = alignment.provider.position(placeable)
                if (linePosition == AlignmentLine.Unspecified) {
                    0
                } else {
                    val offset = lineSpace.before - linePosition
                    if (layoutDirection == LayoutDirection.Ltr) offset else crossAxisSize - placeable.width - offset
                }
            }

            else -> horizontalAlignment.align(placeable.width, crossAxisSize, layoutDirection)
        }
}
