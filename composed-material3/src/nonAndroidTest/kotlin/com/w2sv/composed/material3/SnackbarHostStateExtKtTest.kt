package com.w2sv.composed.material3

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class SnackbarHostStateExtKtTest {

    @Test
    fun `dismissCurrentSnackbar dismisses the currently shown snackbar`() =
        runTest {
            val snackbarHostState = SnackbarHostState()
            var result: SnackbarResult? = null

            backgroundScope.launch {
                result = snackbarHostState.showSnackbar(SnackbarFakes.Visuals())
            }
            runCurrent()

            assertNotNull(snackbarHostState.currentSnackbarData)

            snackbarHostState.dismissCurrentSnackbar()
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, result)
            assertNull(snackbarHostState.currentSnackbarData)
        }

    @Test
    fun `dismissCurrentSnackbar is a no-op when no snackbar is shown`() {
        val snackbarHostState = SnackbarHostState()

        snackbarHostState.dismissCurrentSnackbar()

        assertNull(snackbarHostState.currentSnackbarData)
    }

    @Test
    fun `replaceCurrentWith visuals dismisses the current snackbar before showing the new one`() =
        runTest {
            val snackbarHostState = SnackbarHostState()
            var firstResult: SnackbarResult? = null

            backgroundScope.launch {
                firstResult = snackbarHostState.showSnackbar(SnackbarFakes.Visuals("first"))
            }
            runCurrent()

            assertEquals("first", snackbarHostState.currentSnackbarData?.visuals?.message)

            backgroundScope.launch {
                snackbarHostState.replaceCurrentWith(SnackbarFakes.Visuals("second"))
            }
            runCurrent()

            assertEquals(SnackbarResult.Dismissed, firstResult)
            assertEquals("second", snackbarHostState.currentSnackbarData?.visuals?.message)
        }

    @Test
    fun `replaceCurrentWith parameters shows a snackbar built from the given parameters`() =
        runTest {
            val snackbarHostState = SnackbarHostState()

            backgroundScope.launch {
                snackbarHostState.replaceCurrentWith(
                    message = SnackbarFakes.MESSAGE,
                    actionLabel = "Action",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            }
            runCurrent()

            val visuals = requireNotNull(snackbarHostState.currentSnackbarData?.visuals)
            assertEquals(SnackbarFakes.MESSAGE, visuals.message)
            assertEquals("Action", visuals.actionLabel)
            assertTrue(visuals.withDismissAction)
            assertEquals(SnackbarDuration.Long, visuals.duration)
        }

    @Test
    fun `replaceCurrentWith parameters defaults duration based on actionLabel presence`() =
        runTest {
            val snackbarHostState = SnackbarHostState()

            backgroundScope.launch {
                snackbarHostState.replaceCurrentWith(message = SnackbarFakes.MESSAGE)
            }
            runCurrent()

            assertEquals(SnackbarDuration.Short, snackbarHostState.currentSnackbarData?.visuals?.duration)

            snackbarHostState.dismissCurrentSnackbar()
            runCurrent()

            backgroundScope.launch {
                snackbarHostState.replaceCurrentWith(message = SnackbarFakes.MESSAGE, actionLabel = "Action")
            }
            runCurrent()

            assertEquals(SnackbarDuration.Indefinite, snackbarHostState.currentSnackbarData?.visuals?.duration)
        }
}
