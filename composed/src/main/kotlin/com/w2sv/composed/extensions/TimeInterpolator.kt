package com.w2sv.composed.extensions

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing

fun TimeInterpolator.toEasing() = Easing {
    getInterpolation(it)
}