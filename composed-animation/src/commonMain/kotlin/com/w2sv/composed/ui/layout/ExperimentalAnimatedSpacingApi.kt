package com.w2sv.composed.ui.layout

/**
 * Marks animated spacing layout APIs whose names, measurement semantics, or weight redistribution behavior may change
 * based on usage feedback.
 *
 * Opting in acknowledges that source and binary compatibility is not guaranteed while the API remains experimental.
 */
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
