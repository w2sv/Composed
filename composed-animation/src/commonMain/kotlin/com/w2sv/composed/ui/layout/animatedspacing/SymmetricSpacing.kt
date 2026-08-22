package com.w2sv.composed.ui.layout.animatedspacing

import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Averages forward and reverse gap contributions, then rounds them as one symmetric group. The correction step keeps
 * the integer sum equal to the rounded continuous total instead of letting independently rounded gaps drift.
 */
internal fun calculateSymmetricSpacings(presence: FloatArray, spacing: Int): IntArray {
    if (presence.size <= 1 || spacing == 0) return IntArray(presence.size)

    val idealSpacings = FloatArray(presence.size)
    var accumulatedPresence = 0f
    var totalPresence = 0.0
    var previousGapCount = 0f

    presence.indices.forEach { index ->
        val itemPresence = presence[index].coerceIn(0f, 1f)
        accumulatedPresence += itemPresence
        totalPresence += itemPresence
        val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
        idealSpacings[index] = gapCount - previousGapCount
        previousGapCount = gapCount
    }

    val targetTotal = ((totalPresence - 1.0).coerceAtLeast(0.0) * spacing).roundToInt()
    accumulatedPresence = 0f
    previousGapCount = 0f
    val result = IntArray(presence.size)
    var roundedTotal = 0

    for (index in presence.lastIndex downTo 0) {
        accumulatedPresence += presence[index].coerceIn(0f, 1f)
        val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
        val reverseContribution = gapCount - previousGapCount

        if (index < presence.lastIndex) {
            val gapIndex = index + 1
            idealSpacings[gapIndex] = spacing * (idealSpacings[gapIndex] + reverseContribution) / 2f
            result[gapIndex] = idealSpacings[gapIndex].roundToInt()
            roundedTotal += result[gapIndex]
        }
        previousGapCount = gapCount
    }

    distributeRoundingRemainderSymmetrically(result, idealSpacings, targetTotal - roundedTotal)
    return result
}

private fun distributeRoundingRemainderSymmetrically(
    result: IntArray,
    ideal: FloatArray,
    initialDelta: Int
) {
    var delta = initialDelta
    if (delta == 0) return

    val gapCount = result.size - 1
    if (abs(delta) % 2 == 1 && gapCount % 2 == 1) {
        val center = 1 + gapCount / 2
        if (adjust(result, center, delta.sign)) delta -= delta.sign
    }

    var left = gapCount / 2
    var right = if (gapCount % 2 == 0) left + 1 else left + 2
    while (abs(delta) >= 2 && left >= 1 && right <= result.lastIndex) {
        val unit = delta.sign
        if (canAdjust(result, left, unit) && canAdjust(result, right, unit)) {
            result[left] += unit
            result[right] += unit
            delta -= unit * 2
        }
        left--
        right++
    }

    if (delta != 0) {
        bestCorrectionIndex(result, ideal, delta.sign)?.let { index ->
            if (adjust(result, index, delta.sign)) delta -= delta.sign
        }
    }

    for (index in 1..result.lastIndex) {
        if (delta == 0) break
        if (adjust(result, index, delta.sign)) delta -= delta.sign
    }
}

private fun adjust(
    result: IntArray,
    index: Int,
    amount: Int
): Boolean {
    if (amount < 0 && result[index] == 0) return false
    result[index] += amount
    return true
}

private fun canAdjust(
    result: IntArray,
    index: Int,
    amount: Int
): Boolean =
    amount > 0 || result[index] > 0

private fun bestCorrectionIndex(
    result: IntArray,
    ideal: FloatArray,
    direction: Int
): Int? {
    var bestIndex: Int? = null
    var bestError = Float.NEGATIVE_INFINITY
    val center = result.lastIndex / 2f + 0.5f

    for (index in 1..result.lastIndex) {
        if (direction < 0 && result[index] == 0) continue
        val error = if (direction > 0) ideal[index] - result[index] else result[index] - ideal[index]
        if (error > bestError || (error == bestError && abs(index - center) < abs(checkNotNull(bestIndex) - center))) {
            bestIndex = index
            bestError = error
        }
    }
    return bestIndex
}
