package com.w2sv.composed.navigation3

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/** A navigation destination that owns a nested back stack. */
interface NestedNavHost<T : NavKey> : NavKey {
    val backStack: NavBackStack<T>
}
