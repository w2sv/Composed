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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

internal class AnimatedSpacingColumnMeasurePolicy(private val spacing: Dp, private val horizontalAlignment: Alignment.Horizontal) :
    MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.isEmpty()) return layout(constraints.minWidth, constraints.minHeight) {}

        val parentData = measurables.parentData()
        val presence = parentData.animatedPresenceOrNull()
        val spacingPx = spacing.roundToPx()
        val animatedSpacings = presence?.let { calculateSpacings(it, spacingPx) }
        val totalSpacing = animatedSpacings?.sum() ?: (spacingPx * (measurables.size - 1))
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val weightSummary = measureFixedChildren(measurables, parentData, placeables, constraints, totalSpacing)

        measureWeightedChildren(
            measurables = measurables,
            parentData = parentData,
            presence = presence,
            placeables = placeables,
            constraints = constraints,
            totalSpacing = totalSpacing,
            weightSummary = weightSummary
        )

        val contentSize = placeables.contentSize(totalSpacing)
        val alignmentLineSpace = placeables.alignmentLineSpace(parentData)
        val width = constraints.constrainWidth(maxOf(contentSize.width, alignmentLineSpace.width))
        val height = constraints.constrainHeight(contentSize.height)

        return layout(width, height) {
            var y = 0

            placeables.forEachIndexed { index, placeable ->
                placeable ?: return@forEachIndexed

                y += animatedSpacings.spacingBefore(index, spacingPx)
                val x = placeable.horizontalPosition(
                    parentData = parentData[index],
                    defaultAlignment = horizontalAlignment,
                    alignmentLineSpace = alignmentLineSpace,
                    width = width,
                    layoutDirection = layoutDirection
                )

                placeable.place(x, y)
                y += placeable.height
            }
        }
    }
}

private data class WeightSummary(val fixedHeight: Int, val totalWeight: Float, val hasVisibilityControlledWeight: Boolean)

@JvmInline
private value class AlignmentLineSpace(private val packed: Long) {
    constructor(before: Int, after: Int) : this((before.toLong() shl Int.SIZE_BITS) or (after.toLong() and UINT_MASK))

    val before: Int get() = (packed shr Int.SIZE_BITS).toInt()

    val after: Int get() = packed.toInt()

    val width: Int get() = before + after

    private companion object {
        const val UINT_MASK = 0xffffffffL
    }
}

private fun List<Measurable>.parentData(): Array<AnimatedSpacingColumnParentData?> =
    Array(size) { index ->
        this[index].parentData as? AnimatedSpacingColumnParentData
    }

private fun Array<AnimatedSpacingColumnParentData?>.animatedPresenceOrNull(): FloatArray? {
    if (none { it?.visibilityControlled == true }) return null

    return FloatArray(size) { index ->
        this[index]?.presence?.value?.coerceIn(0f, 1f) ?: 1f
    }
}

private fun IntArray?.spacingBefore(index: Int, ordinarySpacing: Int): Int =
    this?.get(index) ?: if (index == 0) 0 else ordinarySpacing

private fun measureFixedChildren(
    measurables: List<Measurable>,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    placeables: Array<Placeable?>,
    constraints: Constraints,
    totalSpacing: Int
): WeightSummary {
    val boundedHeight = constraints.maxHeight != Constraints.Infinity
    var fixedHeight = 0
    var totalWeight = 0f
    var hasVisibilityControlledWeight = false

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]
        val weight = data?.weight ?: 0f

        if (boundedHeight && weight > 0f) {
            totalWeight += weight
            hasVisibilityControlledWeight = hasVisibilityControlledWeight || data?.visibilityControlled == true
        } else {
            val maxHeight = if (boundedHeight) {
                (constraints.maxHeight - totalSpacing - fixedHeight).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val placeable = measurable.measure(
                Constraints(maxWidth = constraints.maxWidth, maxHeight = maxHeight)
            )

            placeables[index] = placeable
            fixedHeight += placeable.height
        }
    }

    return WeightSummary(fixedHeight, totalWeight, hasVisibilityControlledWeight)
}

private fun measureWeightedChildren(
    measurables: List<Measurable>,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    presence: FloatArray?,
    placeables: Array<Placeable?>,
    constraints: Constraints,
    totalSpacing: Int,
    weightSummary: WeightSummary
) {
    if (constraints.maxHeight == Constraints.Infinity || weightSummary.totalWeight <= 0f) return

    val availableHeight = (constraints.maxHeight - totalSpacing - weightSummary.fixedHeight).coerceAtLeast(0)
    val allocations = calculateWeightAllocations(availableHeight, parentData, presence, weightSummary)

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]?.takeIf { it.weight != null } ?: return@forEachIndexed
        val measurementAllocation = data.measurementAllocation(
            allocation = allocations[index],
            presence = presence?.get(index) ?: 1f,
            availableHeight = availableHeight
        )
        val minHeight = if (data.fill && !data.visibilityControlled) measurementAllocation else 0

        placeables[index] = measurable.measure(
            Constraints(
                maxWidth = constraints.maxWidth,
                minHeight = minHeight,
                maxHeight = measurementAllocation
            )
        )
    }
}

private fun calculateWeightAllocations(
    availableHeight: Int,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    presence: FloatArray?,
    weightSummary: WeightSummary
): IntArray =
    if (weightSummary.hasVisibilityControlledWeight) {
        calculateAnimatedWeightedAllocations(availableHeight, parentData, checkNotNull(presence))
    } else {
        calculateOrdinaryWeightedAllocations(availableHeight, parentData, weightSummary.totalWeight)
    }

private fun AnimatedSpacingColumnParentData.measurementAllocation(
    allocation: Int,
    presence: Float,
    availableHeight: Int
): Int =
    if (visibilityControlled && presence > 0f) {
        (allocation / presence).roundToInt().coerceIn(0, availableHeight)
    } else {
        allocation
    }

private fun Array<Placeable?>.contentSize(totalSpacing: Int): IntSize {
    var width = 0
    var height = totalSpacing

    forEach { placeable ->
        placeable ?: return@forEach
        width = maxOf(width, placeable.width)
        height += placeable.height
    }

    return IntSize(width, height)
}

private fun Array<Placeable?>.alignmentLineSpace(parentData: Array<AnimatedSpacingColumnParentData?>): AlignmentLineSpace {
    var before = 0
    var after = 0

    forEachIndexed { index, placeable ->
        placeable ?: return@forEachIndexed
        val alignment = parentData[index]?.crossAxisAlignment as? CrossAxisAlignment.Relative ?: return@forEachIndexed
        val linePosition = alignment.provider.position(placeable)

        if (linePosition != AlignmentLine.Unspecified) {
            before = maxOf(before, linePosition)
            after = maxOf(after, placeable.width - linePosition)
        }
    }

    return AlignmentLineSpace(before, after)
}

private fun Placeable.horizontalPosition(
    parentData: AnimatedSpacingColumnParentData?,
    defaultAlignment: Alignment.Horizontal,
    alignmentLineSpace: AlignmentLineSpace,
    width: Int,
    layoutDirection: LayoutDirection
): Int =
    when (val alignment = parentData?.crossAxisAlignment) {
        is CrossAxisAlignment.Horizontal -> alignment.alignment.align(this.width, width, layoutDirection)
        is CrossAxisAlignment.Relative -> alignmentLinePosition(alignment, alignmentLineSpace, width, layoutDirection)
        else -> defaultAlignment.align(this.width, width, layoutDirection)
    }

private fun Placeable.alignmentLinePosition(
    alignment: CrossAxisAlignment.Relative,
    alignmentLineSpace: AlignmentLineSpace,
    width: Int,
    layoutDirection: LayoutDirection
): Int {
    val linePosition = alignment.provider.position(this)
    if (linePosition == AlignmentLine.Unspecified) return 0

    val offset = alignmentLineSpace.before - linePosition
    return if (layoutDirection == LayoutDirection.Ltr) offset else width - this.width - offset
}
