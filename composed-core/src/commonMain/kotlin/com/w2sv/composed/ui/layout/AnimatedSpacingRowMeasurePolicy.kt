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
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

internal class AnimatedSpacingRowMeasurePolicy(private val spacing: Dp, private val verticalAlignment: Alignment.Vertical) :
    MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.isEmpty()) return layout(constraints.minWidth, constraints.minHeight) {}

        val parentData = Array(measurables.size) { measurables[it].parentData as? AnimatedSpacingColumnParentData }
        val presence = parentData.animatedPresenceOrNull()
        val spacingPx = spacing.roundToPx()
        val animatedSpacings = presence?.let { calculateSpacings(it, spacingPx) }
        val totalSpacing = animatedSpacings?.sum() ?: spacingPx * (measurables.size - 1)
        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val weightSummary = measureFixedChildren(measurables, parentData, placeables, constraints, totalSpacing)

        measureWeightedChildren(measurables, parentData, presence, placeables, constraints, totalSpacing, weightSummary)

        val contentSize = placeables.contentSize(totalSpacing)
        val alignmentLineSpace = placeables.alignmentLineSpace(parentData)
        val width = constraints.constrainWidth(contentSize.width)
        val height = constraints.constrainHeight(maxOf(contentSize.height, alignmentLineSpace.size))

        return layout(width, height) {
            var x = 0
            placeables.forEachIndexed { index, placeable ->
                placeable ?: return@forEachIndexed
                x += animatedSpacings.spacingBefore(index, spacingPx)
                val y = placeable.verticalPosition(parentData[index], verticalAlignment, alignmentLineSpace, height)
                placeable.placeRelative(x, y)
                x += placeable.width
            }
        }
    }
}

private data class RowWeightSummary(val fixedWidth: Int, val totalWeight: Float, val hasVisibilityControlledWeight: Boolean)

@JvmInline
private value class RowAlignmentLineSpace(private val packed: Long) {
    constructor(before: Int, after: Int) : this((before.toLong() shl Int.SIZE_BITS) or (after.toLong() and UINT_MASK))
    val before: Int get() = (packed shr Int.SIZE_BITS).toInt()
    val size: Int get() = before + packed.toInt()

    private companion object {
        const val UINT_MASK = 0xffffffffL
    }
}

private fun Array<AnimatedSpacingColumnParentData?>.animatedPresenceOrNull(): FloatArray? {
    if (none { it?.visibilityControlled == true }) return null
    return FloatArray(size) { this[it]?.presence?.value?.coerceIn(0f, 1f) ?: 1f }
}

private fun IntArray?.spacingBefore(index: Int, ordinarySpacing: Int): Int =
    this?.get(index) ?: if (index == 0) 0 else ordinarySpacing

private fun measureFixedChildren(
    measurables: List<Measurable>,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    placeables: Array<Placeable?>,
    constraints: Constraints,
    totalSpacing: Int
): RowWeightSummary {
    val boundedWidth = constraints.maxWidth != Constraints.Infinity
    var fixedWidth = 0
    var totalWeight = 0f
    var hasVisibilityControlledWeight = false

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]
        val weight = data?.weight ?: 0f
        if (boundedWidth && weight > 0f) {
            totalWeight += weight
            hasVisibilityControlledWeight = hasVisibilityControlledWeight || data?.visibilityControlled == true
        } else {
            val maxWidth = if (boundedWidth) (constraints.maxWidth - totalSpacing - fixedWidth).coerceAtLeast(0) else Constraints.Infinity
            val placeable = measurable.measure(Constraints(maxWidth = maxWidth, maxHeight = constraints.maxHeight))
            placeables[index] = placeable
            fixedWidth += placeable.width
        }
    }
    return RowWeightSummary(fixedWidth, totalWeight, hasVisibilityControlledWeight)
}

private fun measureWeightedChildren(
    measurables: List<Measurable>,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    presence: FloatArray?,
    placeables: Array<Placeable?>,
    constraints: Constraints,
    totalSpacing: Int,
    summary: RowWeightSummary
) {
    if (constraints.maxWidth == Constraints.Infinity || summary.totalWeight <= 0f) return
    val availableWidth = (constraints.maxWidth - totalSpacing - summary.fixedWidth).coerceAtLeast(0)
    val allocations = if (summary.hasVisibilityControlledWeight) {
        calculateAnimatedWeightedAllocations(availableWidth, parentData, checkNotNull(presence))
    } else {
        calculateOrdinaryWeightedAllocations(availableWidth, parentData, summary.totalWeight)
    }

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]?.takeIf { it.weight != null } ?: return@forEachIndexed
        val itemPresence = presence?.get(index) ?: 1f
        val allocation = allocations[index]
        val measurementAllocation = if (data.visibilityControlled && itemPresence > 0f) {
            (allocation / itemPresence).roundToInt().coerceIn(0, availableWidth)
        } else {
            allocation
        }
        val minWidth = if (data.fill && !data.visibilityControlled) measurementAllocation else 0
        placeables[index] = measurable.measure(
            Constraints(minWidth = minWidth, maxWidth = measurementAllocation, maxHeight = constraints.maxHeight)
        )
    }
}

private fun Array<Placeable?>.contentSize(totalSpacing: Int): IntSize {
    var width = totalSpacing
    var height = 0
    forEach { placeable ->
        placeable ?: return@forEach
        width += placeable.width
        height = maxOf(height, placeable.height)
    }
    return IntSize(width, height)
}

private fun Array<Placeable?>.alignmentLineSpace(parentData: Array<AnimatedSpacingColumnParentData?>): RowAlignmentLineSpace {
    var before = 0
    var after = 0
    forEachIndexed { index, placeable ->
        placeable ?: return@forEachIndexed
        val alignment = parentData[index]?.crossAxisAlignment as? CrossAxisAlignment.Relative ?: return@forEachIndexed
        val linePosition = alignment.provider.position(placeable)
        if (linePosition != AlignmentLine.Unspecified) {
            before = maxOf(before, linePosition)
            after = maxOf(after, placeable.height - linePosition)
        }
    }
    return RowAlignmentLineSpace(before, after)
}

private fun Placeable.verticalPosition(
    parentData: AnimatedSpacingColumnParentData?,
    defaultAlignment: Alignment.Vertical,
    alignmentLineSpace: RowAlignmentLineSpace,
    height: Int
): Int =
    when (val alignment = parentData?.crossAxisAlignment) {
        is CrossAxisAlignment.Vertical -> alignment.alignment.align(this.height, height)

        is CrossAxisAlignment.Relative -> {
            val linePosition = alignment.provider.position(this)
            if (linePosition == AlignmentLine.Unspecified) 0 else alignmentLineSpace.before - linePosition
        }

        else -> defaultAlignment.align(this.height, height)
    }
