package com.w2sv.composed.core.extensions

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

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
    Color(toColorInt())
