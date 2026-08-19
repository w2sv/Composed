package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals

/**
 * Shortcut for `currentSnackbarData?.dismiss()`.
 */
fun SnackbarHostState.dismissCurrentSnackbar() {
    currentSnackbarData?.dismiss()
}

/**
 * Dismisses the currently showing snackbar if there is one and shows a new one with the given [snackbarVisuals].
 * @see SnackbarHostState.showSnackbar
 */
suspend fun SnackbarHostState.replaceCurrentWith(snackbarVisuals: SnackbarVisuals): SnackbarResult {
    dismissCurrentSnackbar()
    return showSnackbar(snackbarVisuals)
}

/**
 * Dismisses the currently showing snackbar if there is one and shows a new one with the given parameters.
 * @see SnackbarHostState.showSnackbar
 */
suspend fun SnackbarHostState.replaceCurrentWith(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
): SnackbarResult {
    dismissCurrentSnackbar()
    return showSnackbar(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration
    )
}
