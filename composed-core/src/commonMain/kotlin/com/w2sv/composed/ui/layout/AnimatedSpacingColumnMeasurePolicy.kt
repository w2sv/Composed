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
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.floor
import kotlin.math.roundToInt

internal class AnimatedSpacingColumnMeasurePolicy(private val spacing: Dp, private val horizontalAlignment: Alignment.Horizontal) :
    MeasurePolicy {

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(constraints.minWidth, constraints.minHeight) {}
        }

        val count = measurables.size

        val parentData = Array(count) { index ->
            measurables[index].parentData as? AnimatedSpacingColumnParentData
        }

        val presence = FloatArray(count) { index ->
            parentData[index]
                ?.presence
                ?.value
                ?.coerceIn(0f, 1f)
                ?: 1f
        }

        val spacings = calculateSpacings(
            presence = presence,
            spacing = spacing.roundToPx()
        )

        val totalSpacing = spacings.sum()
        val placeables = arrayOfNulls<Placeable>(count)
        val boundedHeight = constraints.maxHeight != Constraints.Infinity

        var fixedHeight = 0
        var totalWeight = 0f

        measurables.forEachIndexed { index, measurable ->
            val data = parentData[index]
            val weight = data?.weight ?: 0f

            if (weight > 0f && boundedHeight) {
                totalWeight += weight
                return@forEachIndexed
            }

            val maxHeight = if (boundedHeight) {
                (constraints.maxHeight - totalSpacing - fixedHeight).coerceAtLeast(0)
            } else {
                Constraints.Infinity
            }

            val placeable = measurable.measure(
                Constraints(
                    minWidth = 0,
                    maxWidth = constraints.maxWidth,
                    minHeight = 0,
                    maxHeight = maxHeight
                )
            )

            placeables[index] = placeable
            fixedHeight += placeable.height
        }

        if (boundedHeight && totalWeight > 0f) {
            val availableHeight = (
                constraints.maxHeight -
                    totalSpacing -
                    fixedHeight
                ).coerceAtLeast(0)

            val allocations = calculateWeightedAllocations(
                availableSpace = availableHeight,
                parentData = parentData,
                presence = presence
            )

            measurables.forEachIndexed { index, measurable ->
                val data = parentData[index] ?: return@forEachIndexed
                if (data.weight == null) return@forEachIndexed

                val allocation = allocations[index]
                val itemPresence = presence[index]

                val measurementAllocation =
                    if (data.visibilityControlled && itemPresence > 0f) {
                        (allocation / itemPresence)
                            .roundToInt()
                            .coerceIn(0, availableHeight)
                    } else {
                        allocation
                    }

                val minHeight =
                    if (data.fill && !data.visibilityControlled) {
                        measurementAllocation
                    } else {
                        0
                    }

                placeables[index] = measurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                        minHeight = minHeight,
                        maxHeight = measurementAllocation
                    )
                )
            }
        }

        var contentWidth = 0
        var contentHeight = totalSpacing

        placeables.forEach { placeable ->
            placeable ?: return@forEach

            contentWidth = maxOf(contentWidth, placeable.width)
            contentHeight += placeable.height
        }

        var beforeAlignmentLine = 0
        var afterAlignmentLine = 0

        placeables.forEachIndexed { index, placeable ->
            placeable ?: return@forEachIndexed

            val alignment =
                parentData[index]?.crossAxisAlignment as? CrossAxisAlignment.Relative
                    ?: return@forEachIndexed

            val linePosition = alignment.provider.position(placeable)
            if (linePosition == AlignmentLine.Unspecified) return@forEachIndexed

            beforeAlignmentLine = maxOf(
                beforeAlignmentLine,
                linePosition
            )

            afterAlignmentLine = maxOf(
                afterAlignmentLine,
                placeable.width - linePosition
            )
        }

        val width = constraints.constrainWidth(
            maxOf(
                contentWidth,
                beforeAlignmentLine + afterAlignmentLine
            )
        )

        val height = constraints.constrainHeight(contentHeight)

        return layout(width, height) {
            var y = 0

            placeables.forEachIndexed { index, placeable ->
                placeable ?: return@forEachIndexed

                y += spacings[index]

                val x = when (
                    val alignment = parentData[index]?.crossAxisAlignment
                ) {
                    is CrossAxisAlignment.Horizontal -> {
                        alignment.alignment.align(
                            size = placeable.width,
                            space = width,
                            layoutDirection = layoutDirection
                        )
                    }

                    is CrossAxisAlignment.Relative -> {
                        val linePosition = alignment.provider.position(placeable)

                        if (linePosition == AlignmentLine.Unspecified) {
                            0
                        } else {
                            val offset = beforeAlignmentLine - linePosition

                            if (layoutDirection == LayoutDirection.Ltr) {
                                offset
                            } else {
                                width - placeable.width - offset
                            }
                        }
                    }

                    null -> {
                        horizontalAlignment.align(
                            size = placeable.width,
                            space = width,
                            layoutDirection = layoutDirection
                        )
                    }
                }

                placeable.placeRelative(x, y)
                y += placeable.height
            }
        }
    }
}

/*
 * Calculate the spacing once from top -> bottom and once from
 * bottom -> top, then average both interpretations.
 *
 * This means a disappearing middle child consumes the spacing on
 * both sides symmetrically instead of arbitrarily owning the gap
 * preceding it.
 */
private fun calculateSpacings(presence: FloatArray, spacing: Int): IntArray {
    if (presence.size <= 1 || spacing == 0) {
        return IntArray(presence.size)
    }

    val forward = calculateDirectionalSpacings(
        presence = presence,
        reversed = false
    )

    val reverse = calculateDirectionalSpacings(
        presence = presence,
        reversed = true
    )

    return IntArray(presence.size) { index ->
        if (index == 0) {
            0
        } else {
            (
                spacing *
                    (forward[index] + reverse[index - 1]) /
                    2f
                ).roundToInt()
        }
    }
}

private fun calculateDirectionalSpacings(presence: FloatArray, reversed: Boolean): FloatArray {
    val result = FloatArray(presence.size)

    var accumulatedPresence = 0f
    var previousGapCount = 0f

    repeat(presence.size) { iteration ->
        val index =
            if (reversed) {
                presence.lastIndex - iteration
            } else {
                iteration
            }

        accumulatedPresence +=
            presence[index].coerceIn(0f, 1f)

        val gapCount = (
            accumulatedPresence - 1f
            ).coerceAtLeast(0f)

        result[index] =
            gapCount - previousGapCount

        previousGapCount = gapCount
    }

    return result
}

/*
 * Begin with the normal fully-present weighted allocation.
 *
 * Each item's presence keeps that fraction of its normal allocation.
 * The disappearing part of its allocation is redistributed to visible
 * weighted siblings.
 *
 * Redistribution is symmetric: it depends only on sibling weights and
 * presence, never on child order.
 *
 * At presence 0/1 this produces ordinary Column weight semantics.
 * With one transitioning weighted item it linearly interpolates between
 * the two endpoint layouts.
 */
private fun calculateWeightedAllocations(
    availableSpace: Int,
    parentData: Array<AnimatedSpacingColumnParentData?>,
    presence: FloatArray
): IntArray {
    val count = parentData.size
    val weights = FloatArray(count) { index ->
        parentData[index]?.weight ?: 0f
    }

    val totalWeight = weights.sum()

    if (totalWeight == 0f || availableSpace == 0) {
        return IntArray(count)
    }

    val baseAllocations = FloatArray(count) { index ->
        availableSpace *
            weights[index] /
            totalWeight
    }

    val allocations = FloatArray(count) { index ->
        baseAllocations[index] *
            presence[index].coerceIn(0f, 1f)
    }

    for (source in 0 until count) {
        if (weights[source] == 0f) continue

        val sourcePresence =
            presence[source].coerceIn(0f, 1f)

        val freedSpace =
            baseAllocations[source] *
                (1f - sourcePresence)

        if (freedSpace == 0f) continue

        var recipientWeight = 0f
        var allOtherItemsAbsent = 1f

        for (recipient in 0 until count) {
            if (
                recipient == source ||
                weights[recipient] == 0f
            ) {
                continue
            }

            val recipientPresence =
                presence[recipient].coerceIn(0f, 1f)

            recipientWeight +=
                weights[recipient] * recipientPresence

            allOtherItemsAbsent *=
                1f - recipientPresence
        }

        if (recipientWeight == 0f) continue

        /*
         * If all siblings are disappearing too, some flexible space
         * should itself disappear rather than being endlessly
         * redistributed between disappearing children.
         */
        val redistributionFraction =
            1f - allOtherItemsAbsent

        val redistributedSpace =
            freedSpace * redistributionFraction

        for (recipient in 0 until count) {
            if (
                recipient == source ||
                weights[recipient] == 0f
            ) {
                continue
            }

            val effectiveWeight =
                weights[recipient] *
                    presence[recipient].coerceIn(0f, 1f)

            allocations[recipient] +=
                redistributedSpace *
                effectiveWeight /
                recipientWeight
        }
    }

    return allocations.roundAllocations(
        maximum = availableSpace
    )
}

private fun FloatArray.roundAllocations(maximum: Int): IntArray {
    val result = IntArray(size)

    val target = sum()
        .coerceAtMost(maximum.toFloat())
        .roundToInt()

    var used = 0

    indices.forEach { index ->
        result[index] =
            floor(this[index])
                .toInt()
                .coerceAtLeast(0)

        used += result[index]
    }

    var remainder = target - used

    while (remainder > 0) {
        var distributed = false

        for (index in indices) {
            if (
                remainder == 0 ||
                this[index] <= 0f
            ) {
                continue
            }

            result[index]++
            remainder--
            distributed = true
        }

        if (!distributed) break
    }

    return result
}
