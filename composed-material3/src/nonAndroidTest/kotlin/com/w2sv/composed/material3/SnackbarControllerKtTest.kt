package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class SnackbarControllerKtTest {

    @Test
    fun `isShowing and currentVisuals reflect snackbar presentation state`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState())

            assertFalse(controller.isShowing)
            assertNull(controller.currentVisuals)

            backgroundScope.launch { controller.show(SnackbarFakes.Visuals()) }
            runCurrent()

            assertTrue(controller.isShowing)
            assertEquals(SnackbarFakes.MESSAGE, controller.currentVisuals?.message)

            controller.dismissCurrent()
            runCurrent()

            assertFalse(controller.isShowing)
            assertNull(controller.currentVisuals)
        }

    @Test
    fun `show suspends until the snackbar is dismissed`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState())

            val result = backgroundScope.async {
                controller.show(SnackbarFakes.Visuals())
            }
            runCurrent()

            assertFalse(result.isCompleted)

            controller.dismissCurrent()
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, result.await())
        }

    @Test
    fun `show returns ActionPerformed when the snackbar action is performed`() =
        runTest {
            val snackbarHostState = SnackbarHostState()
            val controller = SnackbarController(snackbarHostState)

            val result = backgroundScope.async {
                controller.show(SnackbarFakes.Visuals())
            }
            runCurrent()

            snackbarHostState.currentSnackbarData?.performAction()
            runCurrent()

            assertEquals(SnackbarResult.ActionPerformed, result.await())
        }

    @Test
    fun `replaceCurrentWith dismisses the current snackbar before showing the new one`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState())

            val firstResult = backgroundScope.async {
                controller.show(SnackbarFakes.Visuals("first"))
            }
            runCurrent()

            val secondResult = backgroundScope.async {
                controller.replaceCurrentWith(SnackbarFakes.Visuals("second"))
            }
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, firstResult.await())
            assertEquals("second", controller.currentVisuals?.message)

            controller.dismissCurrent()
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, secondResult.await())
        }

    @Test
    fun `dismissCurrent is a no-op when no snackbar is shown`() {
        val controller = SnackbarController(SnackbarHostState())

        controller.dismissCurrent()

        assertFalse(controller.isShowing)
        assertNull(controller.currentVisuals)
    }

    @Test
    fun `rememberSnackbarController returns the same instance across recompositions`() =
        runComposeUiTest {
            val snackbarHostState = SnackbarHostState()
            var recomposeTrigger by mutableStateOf(0)
            lateinit var controller: SnackbarController

            setContent {
                recomposeTrigger
                controller = rememberSnackbarController(snackbarHostState)
            }
            waitForIdle()

            val initialController = controller

            runOnIdle { recomposeTrigger++ }
            waitForIdle()

            assertSame(initialController, controller)
        }

    @Test
    fun `rememberSnackbarController creates a new instance when snackbarHostState changes`() =
        runComposeUiTest {
            var snackbarHostState by mutableStateOf(SnackbarHostState())
            lateinit var controller: SnackbarController

            setContent {
                controller = rememberSnackbarController(snackbarHostState)
            }
            waitForIdle()

            val initialController = controller

            runOnIdle { snackbarHostState = SnackbarHostState() }
            waitForIdle()

            assertNotSame(initialController, controller)
        }
}
