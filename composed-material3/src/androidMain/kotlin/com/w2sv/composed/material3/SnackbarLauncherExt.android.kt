package com.w2sv.composed.material3

import android.content.Context
import androidx.compose.material3.SnackbarVisuals

/**
 * Launches showing snackbar visuals created with the associated [Context].
 */
fun SnackbarLauncher.show(makeSnackbar: Context.() -> SnackbarVisuals) {
    launch { show(makeSnackbar) }
}

/**
 * Launches replacement of the currently shown snackbar with visuals created
 * with the associated [Context].
 */
fun SnackbarLauncher.replaceCurrentWith(makeSnackbar: Context.() -> SnackbarVisuals) {
    launch { replaceCurrentWith(makeSnackbar) }
}
