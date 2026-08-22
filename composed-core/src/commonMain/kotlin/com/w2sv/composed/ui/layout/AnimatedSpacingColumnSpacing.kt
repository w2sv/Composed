package com.w2sv.composed.ui.layout

import kotlin.math.roundToInt

/**
 * Average top-to-bottom and bottom-to-top spacing so a disappearing middle child
 * relinquishes its surrounding gaps symmetrically.
 */
internal fun calculateSpacings(presence: FloatArray, spacing: Int): IntArray {
    if (presence.size <= 1 || spacing == 0) return IntArray(presence.size)

    val forward = calculateForwardSpacings(presence)
    val result = IntArray(presence.size)
    var accumulatedPresence = 0f
    var previousGapCount = 0f

    for (index in presence.lastIndex downTo 0) {
        accumulatedPresence += presence[index].coerceIn(0f, 1f)

        val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
        val reverseSpacing = gapCount - previousGapCount

        if (index < presence.lastIndex) {
            result[index + 1] = (spacing * (forward[index + 1] + reverseSpacing) / 2f).roundToInt()
        }

        previousGapCount = gapCount
    }

    return result
}

private fun calculateForwardSpacings(presence: FloatArray): FloatArray {
    val result = FloatArray(presence.size)
    var accumulatedPresence = 0f
    var previousGapCount = 0f

    repeat(presence.size) { index ->
        accumulatedPresence += presence[index].coerceIn(0f, 1f)

        val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
        result[index] = gapCount - previousGapCount
        previousGapCount = gapCount
    }

    return result
}
