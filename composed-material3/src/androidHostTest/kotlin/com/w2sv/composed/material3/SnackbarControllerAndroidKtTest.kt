package com.w2sv.composed.material3

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SnackbarControllerAndroidKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    // ------------------------------------------------------------
    // Common contract, exercised against the Android actual
    // ------------------------------------------------------------

    @Test
    fun `isShowing and currentVisuals reflect snackbar presentation state`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState(), context)

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
            val controller = SnackbarController(SnackbarHostState(), context)

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
            val controller = SnackbarController(snackbarHostState, context)

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
            val controller = SnackbarController(SnackbarHostState(), context)

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
        val controller = SnackbarController(SnackbarHostState(), context)

        controller.dismissCurrent()

        assertFalse(controller.isShowing)
        assertNull(controller.currentVisuals)
    }

    // ------------------------------------------------------------
    // Android-only Context overloads
    // ------------------------------------------------------------

    @Test
    fun `show with Context block uses the controller Context`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState(), context)
            var receivedContext: Context? = null

            backgroundScope.launch {
                controller.show {
                    receivedContext = this
                    SnackbarFakes.Visuals()
                }
            }
            runCurrent()

            assertSame(context, receivedContext)
            assertEquals(SnackbarFakes.MESSAGE, controller.currentVisuals?.message)
        }

    @Test
    fun `replaceCurrentWith Context block uses the controller Context`() =
        runTest {
            val controller = SnackbarController(SnackbarHostState(), context)

            backgroundScope.launch {
                controller.show(SnackbarFakes.Visuals("first"))
            }
            runCurrent()

            var receivedContext: Context? = null
            val result = backgroundScope.async {
                controller.replaceCurrentWith {
                    receivedContext = this
                    SnackbarFakes.Visuals("second")
                }
            }
            runCurrent()

            assertSame(context, receivedContext)
            assertEquals("second", controller.currentVisuals?.message)

            controller.dismissCurrent()
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, result.await())
        }

    // ------------------------------------------------------------
    // rememberSnackbarController
    // ------------------------------------------------------------

    @Test
    fun `rememberSnackbarController uses LocalContext`() {
        val snackbarHostState = SnackbarHostState()
        lateinit var controller: SnackbarController
        lateinit var expectedContext: Context

        composeTestRule.setContent {
            expectedContext = LocalContext.current
            controller = rememberSnackbarController(snackbarHostState)
        }
        composeTestRule.waitForIdle()

        assertSame(expectedContext, controller.context)
    }

    @Test
    fun `rememberSnackbarController returns the same instance across recompositions`() {
        val snackbarHostState = SnackbarHostState()
        var recomposeTrigger by mutableStateOf(0)
        lateinit var controller: SnackbarController

        composeTestRule.setContent {
            recomposeTrigger
            controller = rememberSnackbarController(snackbarHostState)
        }
        composeTestRule.waitForIdle()

        val initialController = controller

        composeTestRule.runOnIdle { recomposeTrigger++ }
        composeTestRule.waitForIdle()

        assertSame(initialController, controller)
    }

    @Test
    fun `rememberSnackbarController creates a new instance when snackbarHostState changes`() {
        var snackbarHostState by mutableStateOf(SnackbarHostState())
        lateinit var controller: SnackbarController

        composeTestRule.setContent {
            controller = rememberSnackbarController(snackbarHostState)
        }
        composeTestRule.waitForIdle()

        val initialController = controller

        composeTestRule.runOnIdle { snackbarHostState = SnackbarHostState() }
        composeTestRule.waitForIdle()

        assertNotSame(initialController, controller)
    }
}
