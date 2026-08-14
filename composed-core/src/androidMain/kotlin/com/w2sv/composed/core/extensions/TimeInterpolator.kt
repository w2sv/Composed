package com.w2sv.composed.core.extensions

import android.animation.TimeInterpolator
import androidx.compose.animation.core.Easing

/**
 * Converts a [TimeInterpolator] from the View animation system into a Compose [Easing].
 * This allows existing interpolators to be reused in Compose animation specs.
 */
fun TimeInterpolator.toEasing() =
    Easing { getInterpolation(it) }
