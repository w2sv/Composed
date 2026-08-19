package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
actual class SnackbarController(actual val snackbarHostState: SnackbarHostState) {

    actual suspend fun show(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showSnackbar(visuals)

    actual suspend fun showReplacing(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showReplacing(visuals)

    actual fun dismissCurrent() {
        snackbarHostState.dismissCurrentSnackbar()
    }
}

@Composable
actual fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController =
    remember(snackbarHostState) { SnackbarController(snackbarHostState) }
