package com.w2sv.composed.ui.layout.animatedspacing

import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sign

/** Matches Foundation's rounded weight-unit allocation and signed remainder distribution. */
internal fun calculateOrdinaryWeightedAllocations(
    availableSpace: Int,
    parentData: Array<AnimatedSpacingParentData?>,
    totalWeight: Float = parentData.fold(0f) { total, data -> total + (data?.weight ?: 0f) }
): IntArray {
    val allocations = IntArray(parentData.size)
    if (totalWeight == 0f || availableSpace == 0) return allocations

    val weightUnitSpace = availableSpace / totalWeight
    var remainder = availableSpace

    parentData.forEach { data ->
        remainder -= (weightUnitSpace * (data?.weight ?: 0f)).roundToInt()
    }

    parentData.forEachIndexed { index, data ->
        val weight = data?.weight ?: 0f
        if (weight <= 0f) return@forEachIndexed

        val remainderUnit = remainder.sign
        remainder -= remainderUnit
        allocations[index] = ((weightUnitSpace * weight).roundToInt() + remainderUnit).coerceAtLeast(0)
    }

    return allocations
}

/**
 * A disappearing weighted child keeps its visible share of its normal allocation.
 * Its remaining share is redistributed symmetrically among visible weighted siblings.
 *
 * This branch intentionally compares every visibility-controlled weighted source with every potential recipient.
 * Ordinary weights never enter it.
 */
internal fun calculateAnimatedWeightedAllocations(
    availableSpace: Int,
    parentData: Array<AnimatedSpacingParentData?>,
    presence: FloatArray
): IntArray {
    val weights = FloatArray(parentData.size) { index -> parentData[index]?.weight ?: 0f }
    val totalWeight = weights.sum()
    if (totalWeight == 0f || availableSpace == 0) return IntArray(parentData.size)

    val baseAllocations = FloatArray(parentData.size) { index -> availableSpace * weights[index] / totalWeight }
    val allocations = FloatArray(parentData.size) { index -> baseAllocations[index] * presence[index].coerceIn(0f, 1f) }

    weights.indices.forEach { source ->
        redistributeFreedSpace(source, weights, baseAllocations, allocations, presence)
    }

    return allocations.roundAllocations(availableSpace)
}

private fun redistributeFreedSpace(
    source: Int,
    weights: FloatArray,
    baseAllocations: FloatArray,
    allocations: FloatArray,
    presence: FloatArray
) {
    if (weights[source] == 0f) return

    val freedSpace = baseAllocations[source] * (1f - presence[source].coerceIn(0f, 1f))
    if (freedSpace == 0f) return

    var recipientWeight = 0f
    var allRecipientsAbsent = 1f

    weights.indices.forEach { recipient ->
        if (recipient == source || weights[recipient] == 0f) return@forEach

        val recipientPresence = presence[recipient].coerceIn(0f, 1f)
        recipientWeight += weights[recipient] * recipientPresence
        allRecipientsAbsent *= 1f - recipientPresence
    }

    if (recipientWeight == 0f) return

    val redistributedSpace = freedSpace * (1f - allRecipientsAbsent)

    weights.indices.forEach { recipient ->
        if (recipient == source || weights[recipient] == 0f) return@forEach

        val effectiveWeight = weights[recipient] * presence[recipient].coerceIn(0f, 1f)
        allocations[recipient] += redistributedSpace * effectiveWeight / recipientWeight
    }
}

private fun FloatArray.roundAllocations(maximum: Int): IntArray {
    val result = IntArray(size)
    val target = sum().coerceAtMost(maximum.toFloat()).roundToInt()
    var used = 0

    indices.forEach { index ->
        result[index] = floor(this[index]).toInt().coerceAtLeast(0)
        used += result[index]
    }

    var remainder = target - used

    while (remainder > 0) {
        var distributed = false
        for (index in indices) {
            if (remainder == 0) break
            if (this[index] <= 0f) continue
            result[index]++
            remainder--
            distributed = true
        }
        if (!distributed) break
    }

    return result
}
