package com.w2sv.composed.core.extensions

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals

/**
 * Dismisses the currently showing snackbar if there is one and shows a new one with the given [snackbarVisuals].
 *
 * @see SnackbarHostState.showSnackbar
 */
suspend fun SnackbarHostState.dismissCurrentSnackbarAndShow(snackbarVisuals: SnackbarVisuals) {
    currentSnackbarData?.dismiss()
    showSnackbar(snackbarVisuals)
}

/**
 * Dismisses the currently showing snackbar if there is one and shows a new one with the given parameters.
 *
 * @see SnackbarHostState.showSnackbar
 */
suspend fun SnackbarHostState.dismissCurrentSnackbarAndShow(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
) {
    currentSnackbarData?.dismiss()
    showSnackbar(
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration
    )
}
