package com.w2sv.composed.ui.layout.animatedspacing

/**
 * Marks implementation that intentionally mirrors behavior in Foundation's
 * `androidx.compose.foundation.layout.RowColumnMeasurePolicy.measure`.
 *
 * Review annotated functions against upstream Foundation changes when updating Compose.
 *
 * @param behavior the specific Foundation behavior preserved by the annotated function.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
internal annotation class FoundationLayoutParity(val behavior: String)
