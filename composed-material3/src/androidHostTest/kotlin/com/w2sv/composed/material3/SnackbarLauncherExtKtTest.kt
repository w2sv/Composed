package com.w2sv.composed.material3

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SnackbarLauncherExtKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `show with Context block launches showing a snackbar built from the launcher's Context`() {
        val snackbarHostState = SnackbarHostState()
        lateinit var launcher: SnackbarLauncher
        lateinit var expectedContext: Context

        composeTestRule.setContent {
            expectedContext = LocalContext.current
            launcher = rememberSnackbarLauncher(snackbarHostState)
        }
        composeTestRule.waitForIdle()

        var receivedContext: Context? = null
        launcher.show {
            receivedContext = this
            SnackbarFakes.Visuals()
        }
        composeTestRule.waitUntil { launcher.isShowing }

        assertSame(expectedContext, receivedContext)
        assertEquals(SnackbarFakes.MESSAGE, launcher.currentVisuals?.message)
    }

    @Test
    fun `replaceCurrentWith with Context block dismisses the current snackbar before showing the new one`() {
        val snackbarHostState = SnackbarHostState()
        lateinit var launcher: SnackbarLauncher

        composeTestRule.setContent {
            launcher = rememberSnackbarLauncher(snackbarHostState)
        }
        composeTestRule.waitForIdle()

        launcher.show(SnackbarFakes.Visuals("first"))
        composeTestRule.waitUntil { launcher.isShowing }

        launcher.replaceCurrentWith { SnackbarFakes.Visuals("second") }
        composeTestRule.waitUntil { launcher.currentVisuals?.message == "second" }
    }
}
