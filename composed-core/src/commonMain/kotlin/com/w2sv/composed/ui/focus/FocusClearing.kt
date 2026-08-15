package com.w2sv.composed.ui.focus

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Coordinates focus-clearing requests with a composition's [FocusManager]. Call
 * [Bind] while requests should be handled.
 *
 * Requests are unbuffered and are dropped without an active binding. Multiple
 * bindings all receive each request. Dispatch stops when [scope] is cancelled.
 *
 * @param scope Scope used to dispatch focus-clearing requests.
 * @param requests Unbuffered stream shared by requesters and active bindings.
 */
@Stable
class FocusClearingController internal constructor(private val scope: CoroutineScope, private val requests: MutableSharedFlow<Unit>) {
    /**
     * Asynchronously requests focus clearing from every active [Bind]. The
     * request is dropped if none is active.
     */
    fun requestClearFocus() {
        scope.launch { requests.emit(Unit) }
    }

    /**
     * Handles requests with [focusManager] while present in the composition.
     * Optionally also clears focus after the IME changes from visible to hidden.
     *
     * IME visibility is inferred from its bottom inset, so hardware or floating
     * keyboards and hosts without IME insets may not be detected. An initially
     * hidden IME does not clear focus. Prefer one binding per controller.
     *
     * @param focusManager Manager on which focus is cleared. Defaults to the
     * current composition's [LocalFocusManager].
     * @param clearOnKeyboardHidden Whether an IME visible-to-hidden transition
     * should clear focus.
     * @param force Passed to [FocusManager.clearFocus]; `true` also releases
     * captured focus.
     */
    @Composable
    fun Bind(
        focusManager: FocusManager = LocalFocusManager.current,
        clearOnKeyboardHidden: Boolean = true,
        force: Boolean = false
    ) {
        val density = LocalDensity.current
        val imeInsets = WindowInsets.ime

        LaunchedEffect(focusManager, force) {
            requests.collectLatest {
                focusManager.clearFocus(force = force)
            }
        }

        if (clearOnKeyboardHidden) {
            LaunchedEffect(focusManager, density, force) {
                var wasKeyboardVisible = false

                snapshotFlow { imeInsets.getBottom(density) > 0 }
                    .distinctUntilChanged()
                    .collect { isKeyboardVisible ->
                        if (wasKeyboardVisible && !isKeyboardVisible) {
                            focusManager.clearFocus(force = force)
                        }
                        wasKeyboardVisible = isKeyboardVisible
                    }
            }
        }
    }
}

/**
 * Remembers a [FocusClearingController] scoped to the current composition.
 *
 * It survives recomposition but is not saveable. Call
 * [FocusClearingController.Bind] before issuing requests.
 *
 * @return A controller whose request-dispatch scope is cancelled on disposal.
 */
@Composable
fun rememberFocusClearingController(): FocusClearingController {
    val scope = rememberCoroutineScope()
    val requests = remember { MutableSharedFlow<Unit>() }
    return remember(scope, requests) {
        FocusClearingController(
            scope = scope,
            requests = requests
        )
    }
}

/**
 * Requests focus clearing when this modifier receives a completed tap gesture.
 *
 * Requires an active [FocusClearingController.Bind]. Drags and cancelled
 * gestures are ignored. Its pointer-input detector may compete with other
 * gesture handlers on the same region.
 *
 * @param controller Controller that receives the focus-clearing request.
 */
fun Modifier.clearFocusOnTap(controller: FocusClearingController): Modifier =
    pointerInput(controller) {
        detectTapGestures {
            controller.requestClearFocus()
        }
    }
