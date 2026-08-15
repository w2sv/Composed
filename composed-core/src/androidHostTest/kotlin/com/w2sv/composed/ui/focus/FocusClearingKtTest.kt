package com.w2sv.composed.ui.focus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FocusClearingKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `requestClearFocus delegates to bound focus manager`() {
        lateinit var controller: FocusClearingController
        val focusManager = RecordingFocusManager()

        composeTestRule.setContent {
            controller = rememberFocusClearingController()
            controller.Bind(
                focusManager = focusManager,
                clearOnKeyboardHidden = false,
                force = true
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.runOnIdle { controller.requestClearFocus() }
        composeTestRule.waitUntil { focusManager.clearFocusCalls.get() == 1 }

        assertEquals(true, focusManager.lastForce.get())
    }

    @Test
    fun `clearFocusOnTap requests focus clearing`() {
        val focusManager = RecordingFocusManager()

        composeTestRule.setContent {
            val controller = rememberFocusClearingController()
            controller.Bind(
                focusManager = focusManager,
                clearOnKeyboardHidden = false
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clearFocusOnTap(controller)
                    .testTag(CLEARING_SURFACE_TAG)
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CLEARING_SURFACE_TAG)
            .performTouchInput { click() }
        composeTestRule.waitUntil { focusManager.clearFocusCalls.get() == 1 }

        assertEquals(false, focusManager.lastForce.get())
    }

    private class RecordingFocusManager : FocusManager {
        val clearFocusCalls = AtomicInteger()
        val lastForce = AtomicReference<Boolean>()

        override fun clearFocus(force: Boolean) {
            lastForce.set(force)
            clearFocusCalls.incrementAndGet()
        }

        override fun moveFocus(focusDirection: FocusDirection): Boolean =
            false
    }

    private companion object {
        const val CLEARING_SURFACE_TAG = "focus-clearing-surface"
    }
}
