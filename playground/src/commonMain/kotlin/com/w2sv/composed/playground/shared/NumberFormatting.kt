package com.w2sv.composed.playground.shared

import kotlin.math.abs
import kotlin.math.roundToInt

// Equivalent of String.format, which is available for JVM only
internal fun Float.toFixed(decimalPlaces: Int): String {
    require(decimalPlaces >= 0)

    if (!isFinite()) return toString()

    val factor = tenToThePowerOf(decimalPlaces)
    val scaled = (this * factor).roundToInt()
    if (decimalPlaces == 0) return scaled.toString()

    val sign = if (scaled < 0) "-" else ""
    val absoluteValue = abs(scaled)
    val whole = absoluteValue / factor
    val fraction = (absoluteValue % factor).toString().padStart(decimalPlaces, '0')
    return "$sign$whole.$fraction"
}

private fun tenToThePowerOf(exponent: Int): Int {
    var result = 1
    repeat(exponent) { result *= 10 }
    return result
}
