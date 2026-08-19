package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

object SnackbarFakes {
    data class Visuals(
        override val message: String = MESSAGE,
        override val actionLabel: String? = null,
        override val withDismissAction: Boolean = false,
        override val duration: SnackbarDuration = SnackbarDuration.Indefinite
    ) : SnackbarVisuals

    const val MESSAGE = "message"
}
