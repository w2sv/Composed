package com.w2sv.composed.ui.graphics

import androidx.compose.ui.graphics.Color

private val namedColors =
    mapOf(
        "black" to 0xFF000000u,
        "darkgray" to 0xFF444444u,
        "gray" to 0xFF888888u,
        "lightgray" to 0xFFCCCCCCu,
        "white" to 0xFFFFFFFFu,
        "red" to 0xFFFF0000u,
        "green" to 0xFF00FF00u,
        "blue" to 0xFF0000FFu,
        "yellow" to 0xFFFFFF00u,
        "cyan" to 0xFF00FFFFu,
        "magenta" to 0xFFFF00FFu,
        "aqua" to 0xFF00FFFFu,
        "darkgrey" to 0xFF444444u,
        "fuchsia" to 0xFFFF00FFu,
        "grey" to 0xFF888888u,
        "lightgrey" to 0xFFCCCCCCu,
        "lime" to 0xFF00FF00u,
        "maroon" to 0xFF800000u,
        "navy" to 0xFF000080u,
        "olive" to 0xFF808000u,
        "purple" to 0xFF800080u,
        "silver" to 0xFFC0C0C0u,
        "teal" to 0xFF008080u
    )

/**
 * Supported formats are:
 * ```
 * #RRGGBB
 * #AARRGGBB
 * ```
 *
 * The following names are also accepted: "red", "blue", "green", "black", "white",
 * "gray", "cyan", "magenta", "yellow", "lightgray", "darkgray",
 * "grey", "lightgrey", "darkgrey", "aqua", "fuchsia", "lime",
 * "maroon", "navy", "olive", "purple", "silver", "teal".
 *
 * @throws IllegalArgumentException if this [String] cannot be parsed.
 */
fun String.toComposeColor(): Color =
    Color(toColorValue())

private fun String.toColorValue(): Int {
    val color =
        if (startsWith("#")) {
            when (length) {
                7 -> 0xFF000000u or substring(1).toUInt(16)
                9 -> substring(1).toUInt(16)
                else -> null
            }
        } else {
            namedColors[lowercase()]
        }

    return color?.toInt() ?: throw IllegalArgumentException("Unknown color: $this")
}
