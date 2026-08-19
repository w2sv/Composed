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
actual class SnackbarController(actual val snackbarHostState: SnackbarHostState, private val context: Context) {
    actual suspend fun show(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showSnackbar(visuals)

    suspend fun show(makeSnackbar: Context.() -> SnackbarVisuals): SnackbarResult =
        show(context.makeSnackbar())

    actual suspend fun showReplacing(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showReplacing(visuals)

    suspend fun showReplacing(makeSnackbar: Context.() -> SnackbarVisuals): SnackbarResult =
        showReplacing(context.makeSnackbar())
    actual fun dismissCurrent() {
        snackbarHostState.dismissCurrentSnackbar()
    }
}

@Composable
actual fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController {
    val context = LocalContext.current
    return remember(snackbarHostState) {
        SnackbarController(snackbarHostState, context)
    }
}
