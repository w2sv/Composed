package com.w2sv.composed.core.extensions

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing

fun TimeInterpolator.toEasing() =
    Easing { getInterpolation(it) }
