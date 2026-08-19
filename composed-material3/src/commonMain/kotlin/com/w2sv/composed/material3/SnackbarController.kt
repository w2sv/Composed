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

    val snackbarHostState: SnackbarHostState

    /**
     * Shows a snackbar and suspends until it is dismissed or its action is performed.
     */
    suspend fun show(visuals: SnackbarVisuals): SnackbarResult

    /**
     * Dismisses the currently shown snackbar before showing [visuals].
     *
     * Suspends until the replacement snackbar is dismissed or its action is performed.
     */
    suspend fun showReplacing(visuals: SnackbarVisuals): SnackbarResult

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent()
}

/**
 * Creates and remembers a [SnackbarController] for [snackbarHostState].
 */
@Composable
expect fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController
