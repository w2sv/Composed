package com.w2sv.composed.material3

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Stable
actual class SnackbarController(private val snackbarHostState: SnackbarHostState, internal val context: Context) {

    actual val isShowing: Boolean get() = snackbarHostState.currentSnackbarData != null
    actual val currentVisuals: SnackbarVisuals? get() = snackbarHostState.currentSnackbarData?.visuals

    actual suspend fun show(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showSnackbar(visuals)

    /**
     * Shows snackbar visuals created with this controller's [Context].
     *
     * Suspends until the snackbar is dismissed or its action is performed.
     */
    suspend fun show(makeSnackbar: Context.() -> SnackbarVisuals): SnackbarResult =
        show(context.makeSnackbar())

    actual suspend fun replaceCurrentWith(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.replaceCurrentWith(visuals)

    /**
     * Dismisses the currently shown snackbar before showing visuals created with
     * this controller's [Context].
     *
     * Suspends until the replacement snackbar is dismissed or its action is performed.
     */
    suspend fun replaceCurrentWith(makeSnackbar: Context.() -> SnackbarVisuals): SnackbarResult =
        replaceCurrentWith(context.makeSnackbar())

    actual fun dismissCurrent() {
        snackbarHostState.dismissCurrentSnackbar()
    }
}

@Composable
actual fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController {
    val context = LocalContext.current
    return remember(snackbarHostState, context) {
        SnackbarController(snackbarHostState, context)
    }
}
