package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

/**
 * Non-suspending façade over [SnackbarController].
 *
 * Snackbar operations are launched in the associated [CoroutineScope], making
 * this controller convenient for fire-and-forget usage from event handlers.
 */
@Stable
expect class SnackbarLauncher {

    /**
     * Launches showing [visuals] in the associated coroutine scope.
     */
    fun show(visuals: SnackbarVisuals)

    /**
     * Launches replacement of the currently shown snackbar with [visuals].
     */
    fun showReplacing(visuals: SnackbarVisuals)

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent()
}

/**
 * Creates and remembers a [SnackbarLauncher].
 */
@Composable
expect fun rememberSnackbarLauncher(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = rememberCoroutineScope()
): SnackbarLauncher
