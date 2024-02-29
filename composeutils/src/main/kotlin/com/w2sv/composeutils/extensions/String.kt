package com.w2sv.composeutils.extensions

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun String.toComposeColor(): Color =
    Color(toColorInt())
