package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * Controls snackbar presentation through [SnackbarHostState].
 *
 * Provides explicit operations for showing, replacing, and dismissing snackbars
 * while preserving the suspending semantics of [SnackbarHostState].
 */
@Stable
expect class SnackbarController {

    /**
     * Whether a snackbar is currently being shown.
     */
    val isShowing: Boolean

    /**
     * Visuals of the currently shown snackbar, or `null` if none is shown.
     */
    val currentVisuals: SnackbarVisuals?

    /**
     * Shows [visuals] and suspends until the snackbar is dismissed or its action is performed.
     */
    suspend fun show(visuals: SnackbarVisuals): SnackbarResult

    /**
     * Dismisses the currently shown snackbar before showing [visuals].
     *
     * Suspends until the replacement snackbar is dismissed or its action is performed.
     */
    suspend fun replaceCurrentWith(visuals: SnackbarVisuals): SnackbarResult

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent()
}

/**
 * Creates and remembers a [SnackbarController] for [snackbarHostState].
 *
 * The controller is recreated when [snackbarHostState] changes.
 */
@Composable
expect fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController
