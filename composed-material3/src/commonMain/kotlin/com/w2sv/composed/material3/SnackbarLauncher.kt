package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Non-suspending façade over [SnackbarController].
 *
 * Snackbar operations are launched in the owned [CoroutineScope], making
 * this launcher convenient for fire-and-forget usage from event handlers.
 */
@Stable
class SnackbarLauncher internal constructor(private val controller: SnackbarController, private val scope: CoroutineScope) {
    /**
     * Whether a snackbar is currently being shown.
     */
    val isShowing: Boolean by controller::isShowing

    /**
     * Visuals of the currently shown snackbar, or `null` if none is shown.
     */
    val currentVisuals: SnackbarVisuals? by controller::currentVisuals

    /**
     * Launches showing [visuals] in the associated coroutine scope.
     */
    fun show(visuals: SnackbarVisuals) {
        launch { show(visuals) }
    }

    /**
     * Launches replacement of the currently shown snackbar with [visuals].
     */
    fun replaceCurrentWith(visuals: SnackbarVisuals) {
        launch { replaceCurrentWith(visuals) }
    }

    /**
     * Dismisses the currently shown snackbar, if any.
     */
    fun dismissCurrent() {
        controller.dismissCurrent()
    }

    internal fun launch(block: suspend SnackbarController.() -> Unit) {
        scope.launch { controller.block() }
    }
}

/**
 * Creates and remembers a [SnackbarLauncher] for [snackbarHostState].
 *
 * Snackbar operations are launched in [scope]. The launcher is recreated when
 * either [snackbarHostState] or [scope] changes.
 */
@Composable
fun rememberSnackbarLauncher(snackbarHostState: SnackbarHostState, scope: CoroutineScope = rememberCoroutineScope()): SnackbarLauncher {
    val controller = rememberSnackbarController(snackbarHostState)
    return remember(controller, scope) {
        SnackbarLauncher(
            controller = controller,
            scope = scope
        )
    }
}
