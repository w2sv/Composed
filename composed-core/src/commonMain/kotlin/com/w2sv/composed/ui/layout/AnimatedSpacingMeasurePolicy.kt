package com.w2sv.composed.ui.layout

import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Measurable
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

internal enum class AnimatedSpacingOrientation { Horizontal, Vertical }

internal interface CrossAxisPosition {
    fun position(
        placeable: Placeable,
        parentData: AnimatedSpacingColumnParentData?,
        lineSpace: AlignmentLineSpace,
        crossAxisSize: Int,
        layoutDirection: LayoutDirection
    ): Int
}

@JvmInline
internal value class AlignmentLineSpace(private val packed: Long) {
    constructor(before: Int, after: Int) : this((before.toLong() shl Int.SIZE_BITS) or (after.toLong() and UINT_MASK))
    val before: Int get() = (packed shr Int.SIZE_BITS).toInt()
    val size: Int get() = before + packed.toInt()

    private companion object {
        const val UINT_MASK = 0xffffffffL
    }
}

private data class WeightSummary(val fixedSpace: Int, val totalWeight: Float, val hasVisibilityControlledWeight: Boolean)

internal fun MeasureScope.measureAnimatedSpacing(
    measurables: List<Measurable>,
    constraints: Constraints,
    spacing: Dp,
    orientation: AnimatedSpacingOrientation,
    crossAxisPosition: CrossAxisPosition
): MeasureResult {
    if (measurables.isEmpty()) return layout(constraints.minWidth, constraints.minHeight) {}

    val parentData = Array(measurables.size) { measurables[it].parentData as? AnimatedSpacingColumnParentData }
    val presence = parentData.animatedPresenceOrNull()
    val spacingPx = spacing.roundToPx()
    val animatedSpacings = presence?.let { calculateSpacings(it, spacingPx) }
    val totalSpacing = animatedSpacings?.sum() ?: spacingPx * (measurables.size - 1)
    val placeables = arrayOfNulls<Placeable>(measurables.size)
    val weights = measureFixedChildren(measurables, parentData, placeables, constraints, totalSpacing, orientation)
    measureWeightedChildren(measurables, parentData, presence, placeables, constraints, totalSpacing, weights, orientation)

    val contentSize = placeables.contentSize(totalSpacing, orientation)
    val lineSpace = placeables.alignmentLineSpace(parentData, orientation)
    val mainSize = constraints.constrainMain(contentSize.main(orientation), orientation)
    val crossSize = constraints.constrainCross(maxOf(contentSize.cross(orientation), lineSpace.size), orientation)
    val resultSize = orientation.size(mainSize, crossSize)

    return layout(resultSize.width, resultSize.height) {
        var mainPosition = 0
        placeables.forEachIndexed { index, placeable ->
            placeable ?: return@forEachIndexed
            mainPosition += animatedSpacings.spacingBefore(index, spacingPx)
            val crossPosition = crossAxisPosition.position(placeable, parentData[index], lineSpace, crossSize, layoutDirection)
            if (orientation == AnimatedSpacingOrientation.Vertical) {
                placeable.place(crossPosition, mainPosition)
            } else {
                placeable.placeRelative(mainPosition, crossPosition)
            }
            mainPosition += placeable.main(orientation)
        }
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
    totalSpacing: Int,
    orientation: AnimatedSpacingOrientation
): WeightSummary {
    val bounded = constraints.maxMain(orientation) != Constraints.Infinity
    var fixedSpace = 0
    var totalWeight = 0f
    var hasVisibilityControlledWeight = false

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]
        val weight = data?.weight ?: 0f
        if (bounded && weight > 0f) {
            totalWeight += weight
            hasVisibilityControlledWeight = hasVisibilityControlledWeight || data?.visibilityControlled == true
        } else {
            val remaining = if (bounded) {
                (constraints.maxMain(orientation) - totalSpacing - fixedSpace).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }
            val placeable = measurable.measure(constraints.fixedChildConstraints(remaining, orientation))
            placeables[index] = placeable
            fixedSpace += placeable.main(orientation)
        }
    }
    return WeightSummary(fixedSpace, totalWeight, hasVisibilityControlledWeight)
}

private fun measureWeightedChildren(
    measurables: List<Measurable>,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    presence: FloatArray?,
    placeables: Array<Placeable?>,
    constraints: Constraints,
    totalSpacing: Int,
    summary: WeightSummary,
    orientation: AnimatedSpacingOrientation
) {
    val maximum = constraints.maxMain(orientation)
    if (maximum == Constraints.Infinity || summary.totalWeight <= 0f) return

    val available = (maximum - totalSpacing - summary.fixedSpace).coerceAtLeast(0)
    val allocations = if (summary.hasVisibilityControlledWeight) {
        calculateAnimatedWeightedAllocations(available, parentData, checkNotNull(presence))
    } else {
        calculateOrdinaryWeightedAllocations(available, parentData, summary.totalWeight)
    }

    measurables.forEachIndexed { index, measurable ->
        val data = parentData[index]?.takeIf { it.weight != null } ?: return@forEachIndexed
        val itemPresence = presence?.get(index) ?: 1f
        val allocation = allocations[index]
        val measuredAllocation = if (data.visibilityControlled && itemPresence > 0f) {
            (allocation / itemPresence).roundToInt().coerceIn(0, available)
        } else {
            allocation
        }
        val minimum = if (data.fill && !data.visibilityControlled) measuredAllocation else 0
        placeables[index] = measurable.measure(constraints.weightedChildConstraints(minimum, measuredAllocation, orientation))
    }
}

private fun Array<Placeable?>.contentSize(totalSpacing: Int, orientation: AnimatedSpacingOrientation): IntSize {
    var main = totalSpacing
    var cross = 0
    forEach { placeable ->
        placeable ?: return@forEach
        main += placeable.main(orientation)
        cross = maxOf(cross, placeable.cross(orientation))
    }
    return orientation.size(main, cross)
}

private fun Array<Placeable?>.alignmentLineSpace(
    parentData: Array<AnimatedSpacingColumnParentData?>,
    orientation: AnimatedSpacingOrientation
): AlignmentLineSpace {
    var before = 0
    var after = 0
    forEachIndexed { index, placeable ->
        placeable ?: return@forEachIndexed
        val alignment = parentData[index]?.crossAxisAlignment as? CrossAxisAlignment.Relative ?: return@forEachIndexed
        val line = alignment.provider.position(placeable)
        if (line != AlignmentLine.Unspecified) {
            before = maxOf(before, line)
            after = maxOf(after, placeable.cross(orientation) - line)
        }
    }
    return AlignmentLineSpace(before, after)
}

private fun Constraints.maxMain(orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) maxHeight else maxWidth

private fun Constraints.fixedChildConstraints(remaining: Int, orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) {
        Constraints(maxWidth = maxWidth, maxHeight = remaining)
    } else {
        Constraints(maxWidth = remaining, maxHeight = maxHeight)
    }

private fun Constraints.weightedChildConstraints(
    minimum: Int,
    maximum: Int,
    orientation: AnimatedSpacingOrientation
) =
    if (orientation == AnimatedSpacingOrientation.Vertical) {
        Constraints(maxWidth = maxWidth, minHeight = minimum, maxHeight = maximum)
    } else {
        Constraints(minWidth = minimum, maxWidth = maximum, maxHeight = maxHeight)
    }

private fun Constraints.constrainMain(value: Int, orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) constrainHeight(value) else constrainWidth(value)

private fun Constraints.constrainCross(value: Int, orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) constrainWidth(value) else constrainHeight(value)

private fun Placeable.main(orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) height else width

private fun Placeable.cross(orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) width else height

private fun IntSize.main(orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) height else width

private fun IntSize.cross(orientation: AnimatedSpacingOrientation) =
    if (orientation == AnimatedSpacingOrientation.Vertical) width else height

private fun AnimatedSpacingOrientation.size(main: Int, cross: Int) =
    if (this == AnimatedSpacingOrientation.Vertical) IntSize(cross, main) else IntSize(main, cross)
