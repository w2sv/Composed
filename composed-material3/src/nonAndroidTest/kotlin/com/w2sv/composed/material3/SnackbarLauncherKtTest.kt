package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.v2.runComposeUiTest
import com.w2sv.composed.material3.SnackbarFakes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class SnackbarLauncherKtTest {

    @Test
    fun `show launches snackbar in the associated scope`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            lateinit var launcher: SnackbarLauncher

            setContent {
                launcher = rememberSnackbarLauncher(snackbarHostState)
            }
            waitForIdle()

            assertFalse(launcher.isShowing)
            assertNull(launcher.currentVisuals)

            launcher.show(SnackbarFakes.Visuals())
            waitUntil { launcher.isShowing }

            assertEquals(SnackbarFakes.MESSAGE, launcher.currentVisuals?.message)
        }

    @Test
    fun `show does not launch when the associated scope is cancelled`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            val scope = CoroutineScope(Job())
            lateinit var launcher: SnackbarLauncher

            setContent {
                launcher = rememberSnackbarLauncher(snackbarHostState, scope)
            }
            waitForIdle()

            scope.cancel()
            launcher.show(SnackbarFakes.Visuals())
            waitForIdle()

            assertFalse(launcher.isShowing)
            assertNull(launcher.currentVisuals)
        }

    @Test
    fun `replaceCurrentWith dismisses the current snackbar before showing the new one`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            lateinit var launcher: SnackbarLauncher

            setContent {
                launcher = rememberSnackbarLauncher(snackbarHostState)
            }
            waitForIdle()

            launcher.show(SnackbarFakes.Visuals("first"))
            waitUntil { launcher.currentVisuals?.message == "first" }

            launcher.replaceCurrentWith(SnackbarFakes.Visuals("second"))
            waitUntil { launcher.currentVisuals?.message == "second" }

            assertTrue(launcher.isShowing)
        }

    @Test
    fun `dismissCurrent dismisses the currently shown snackbar`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            lateinit var launcher: SnackbarLauncher

            setContent {
                launcher = rememberSnackbarLauncher(snackbarHostState)
            }
            waitForIdle()

            launcher.show(SnackbarFakes.Visuals())
            waitUntil { launcher.isShowing }

            launcher.dismissCurrent()
            waitUntil { !launcher.isShowing }

            assertNull(launcher.currentVisuals)
        }

    @Test
    fun `rememberSnackbarLauncher returns the same instance across recompositions`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            var recomposeTrigger by mutableStateOf(0)
            lateinit var launcher: SnackbarLauncher

            setContent {
                recomposeTrigger
                launcher = rememberSnackbarLauncher(snackbarHostState)
            }
            waitForIdle()

            val initialLauncher = launcher

            runOnIdle { recomposeTrigger++ }
            waitForIdle()

            assertSame(initialLauncher, launcher)
        }

    @Test
    fun `rememberSnackbarLauncher creates a new instance when snackbarHostState changes`() =
        runComposeUiTest {
            var snackbarHostState by mutableStateOf(SnackbarHostState())
            lateinit var launcher: SnackbarLauncher

            setContent {
                launcher = rememberSnackbarLauncher(snackbarHostState)
            }
            waitForIdle()

            val initialLauncher = launcher

            runOnIdle { snackbarHostState = SnackbarHostState() }
            waitForIdle()

            assertNotSame(initialLauncher, launcher)
        }

    @Test
    fun `rememberSnackbarLauncher creates a new instance when scope changes`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            val initialScope = CoroutineScope(Job())
            val newScope = CoroutineScope(Job())
            var scope by mutableStateOf<CoroutineScope>(initialScope)
            lateinit var launcher: SnackbarLauncher

            try {
                setContent {
                    launcher = rememberSnackbarLauncher(snackbarHostState, scope)
                }
                waitForIdle()

                val initialLauncher = launcher

                runOnIdle { scope = newScope }
                waitForIdle()

                assertNotSame(initialLauncher, launcher)
            } finally {
                initialScope.cancel()
                newScope.cancel()
            }
        }
}
