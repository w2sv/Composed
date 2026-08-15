package com.w2sv.composed.ui.graphics

import androidx.compose.ui.graphics.Color

/**
 * Parses [hexColor] as a [Color].
 *
 * Supported formats are:
 * ```
 * #RRGGBB
 * #AARRGGBB
 * ```
 *
 * @throws IllegalArgumentException if [hexColor] cannot be parsed.
 */
fun Color.Companion.parse(hexColor: String): Color {
    val color = when (hexColor.length) {
        7 ->
            hexColor
                .takeIf { it.startsWith("#") }
                ?.substring(1)
                ?.toUIntOrNull(16)
                ?.let { 0xFF000000u or it }

        9 ->
            hexColor
                .takeIf { it.startsWith("#") }
                ?.substring(1)
                ?.toUIntOrNull(16)

        else -> null
    }

    return color
        ?.let { Color(it.toInt()) }
        ?: throw IllegalArgumentException("Unknown color: $hexColor")
}
