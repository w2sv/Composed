package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Controls snackbar presentation through [SnackbarHostState].
 *
 * Provides explicit operations for showing, replacing, and dismissing snackbars
 * while preserving the suspending semantics of [SnackbarHostState].
 */
@Stable
class SnackbarController(val snackbarHostState: SnackbarHostState) {

    /**
     * Shows a snackbar and suspends until it is dismissed or its action is performed.
     */
    suspend fun show(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showSnackbar(visuals)

    /**
     * Dismisses the currently shown snackbar before showing [visuals].
     *
     * Suspends until the replacement snackbar is dismissed or its action is performed.
     */
    suspend fun showReplacing(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.dismissCurrentSnackbarAndShow(visuals)

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent() {
        snackbarHostState.dismissCurrentSnackbar()
    }
}

/**
 * Creates and remembers a [SnackbarController] for [snackbarHostState].
 */
@Composable
fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController =
    remember(snackbarHostState) {
        SnackbarController(snackbarHostState)
    }

/**
 * Non-suspending façade over [SnackbarController].
 *
 * Snackbar operations are launched in the associated [CoroutineScope], making
 * this controller convenient for fire-and-forget usage from event handlers.
 */
@Stable
class ScopedSnackbarController internal constructor(private val controller: SnackbarController, private val scope: CoroutineScope) {

    /**
     * Launches showing [visuals] in the associated coroutine scope.
     */
    fun show(visuals: SnackbarVisuals) {
        scope.launch { controller.show(visuals) }
    }

    /**
     * Launches replacement of the currently shown snackbar with [visuals].
     */
    fun showReplacing(visuals: SnackbarVisuals) {
        scope.launch { controller.showReplacing(visuals) }
    }

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent() {
        controller.dismissCurrent()
    }
}

/**
 * Creates and remembers a [ScopedSnackbarController].
 */
@Composable
fun rememberScopedSnackbarController(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = rememberCoroutineScope()
): ScopedSnackbarController {
    val controller = rememberSnackbarController(snackbarHostState)
    return remember(controller, scope) {
        ScopedSnackbarController(
            controller = controller,
            scope = scope
        )
    }
}
