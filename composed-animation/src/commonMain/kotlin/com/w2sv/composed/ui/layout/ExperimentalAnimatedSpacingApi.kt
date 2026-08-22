package com.w2sv.composed.ui.layout

/** Marks APIs for animated spacing layouts that may change incompatibly. */
@RequiresOptIn(
    message = "Animated spacing layout APIs are experimental and may change without notice.",
    level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS
)
annotation class ExperimentalAnimatedSpacingApi
