package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
actual class SnackbarLauncher internal constructor(private val controller: SnackbarController, private val scope: CoroutineScope) {

    actual fun show(visuals: SnackbarVisuals) {
        scope.launch { controller.show(visuals) }
    }

    actual fun showReplacing(visuals: SnackbarVisuals) {
        scope.launch { controller.showReplacing(visuals) }
    }

    actual fun dismissCurrent() {
        controller.dismissCurrent()
    }
}

@Composable
actual fun rememberSnackbarLauncher(snackbarHostState: SnackbarHostState, scope: CoroutineScope): SnackbarLauncher {
    val controller = rememberSnackbarController(snackbarHostState)
    return remember(controller, scope) {
        SnackbarLauncher(
            controller = controller,
            scope = scope
        )
    }
}
