package com.w2sv.composed.material3

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
actual class SnackbarLauncher internal constructor(
    private val controller: SnackbarController,
    private val scope: CoroutineScope,
    private val context: Context
) {
    actual fun show(visuals: SnackbarVisuals) {
        scope.launch { controller.show(visuals) }
    }

    fun show(makeSnackbar: Context.() -> SnackbarVisuals) =
        scope.launch { show(context.makeSnackbar()) }

    actual fun showReplacing(visuals: SnackbarVisuals) {
        scope.launch { controller.showReplacing(visuals) }
    }

    fun showReplacing(makeSnackbar: Context.() -> SnackbarVisuals) =
        scope.launch { showReplacing(context.makeSnackbar()) }

    actual fun dismissCurrent() {
        controller.dismissCurrent()
    }
}

@Composable
actual fun rememberSnackbarLauncher(snackbarHostState: SnackbarHostState, scope: CoroutineScope): SnackbarLauncher {
    val controller = rememberSnackbarController(snackbarHostState)
    val context = LocalContext.current
    return remember(controller, scope, context) {
        SnackbarLauncher(
            controller = controller,
            scope = scope,
            context = context
        )
    }
}
