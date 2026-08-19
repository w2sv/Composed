package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
actual class SnackbarController(private val snackbarHostState: SnackbarHostState) {

    actual val isShowing: Boolean get() = snackbarHostState.currentSnackbarData != null
    actual val currentVisuals: SnackbarVisuals? get() = snackbarHostState.currentSnackbarData?.visuals

    actual suspend fun show(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.showSnackbar(visuals)

    actual suspend fun replaceCurrentWith(visuals: SnackbarVisuals): SnackbarResult =
        snackbarHostState.replaceCurrentWith(visuals)

    actual fun dismissCurrent() {
        snackbarHostState.dismissCurrentSnackbar()
    }
}

@Composable
actual fun rememberSnackbarController(snackbarHostState: SnackbarHostState): SnackbarController =
    remember(snackbarHostState) { SnackbarController(snackbarHostState) }
