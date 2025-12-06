package com.w2sv.composed.core

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FlowCollectionKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // ------------------------------------------------------------
    // CollectFromFlow
    // ------------------------------------------------------------

    @Test
    fun `CollectFromFlow collects initial emissions`() =
        runTest {
            val flow = MutableSharedFlow<Int>()
            val collected = mutableListOf<Int>()

            composeTestRule.setContent {
                CollectFromFlow(flow) { value -> collected.add(value) }
            }

            flow.emit(1)
            flow.emit(2)

            assertEquals(listOf(1, 2), collected)
        }

    @Test
    fun `CollectFromFlow restarts collection when key changes`() =
        runTest {
            val flow = flowOf(1, 2)
            val collected = mutableListOf<Int>()
            var key by mutableStateOf("A")

            composeTestRule.setContent {
                CollectFromFlow(
                    flow = flow,
                    key1 = key,
                    collector = { collected.add(it) }
                )
            }

            composeTestRule.runOnIdle { key = "B" }
            composeTestRule.waitForIdle()

            assertEquals(listOf(1, 2, 1, 2), collected)
        }

    @Test
    fun `CollectFromFlow updates collector`() =
        runTest {
            val flow = MutableSharedFlow<Int>()

            var firstCount = 0
            var secondCount = 0

            var collector: suspend (Int) -> Unit by mutableStateOf({ firstCount++ })

            composeTestRule.setContent {
                CollectFromFlow(flow, collector = collector)
            }

            flow.emit(1)
            composeTestRule.runOnIdle { collector = { secondCount++ } } // should be picked up
            composeTestRule.waitForIdle()
            flow.emit(1)

            assertEquals(1, firstCount)
            assertEquals(1, secondCount)
        }

    // ------------------------------------------------------------
    // CollectLatestFromFlow
    // ------------------------------------------------------------

    @Test
    fun `CollectLatestFromFlow collects values`() =
        runTest {
            val flow = MutableSharedFlow<Int>()
            val received = mutableListOf<Int>()

            composeTestRule.setContent {
                CollectLatestFromFlow(flow) { received.add(it) }
            }

            flow.emit(10)
            flow.emit(20)

            assertEquals(listOf(10, 20), received)
        }

    @Test
    fun `CollectLatestFromFlow restarts when key changes`() {
        val flow = flowOf(1, 2)
        val collected = mutableListOf<Int>()
        var key by mutableStateOf("A")

        composeTestRule.setContent {
            CollectLatestFromFlow(
                flow = flow,
                key1 = key,
                action = { collected.add(it) }
            )
        }

        composeTestRule.runOnIdle { key = "B" }
        composeTestRule.waitForIdle()

        assertEquals(listOf(1, 2, 1, 2), collected)
    }

    @Test
    fun `CollectLatestFromFlow updates action`() =
        runTest {
            val flow = MutableSharedFlow<Int>()

            var first = 0
            var second = 0
            var action: suspend (Int) -> Unit by mutableStateOf({ first++ })

            composeTestRule.setContent {
                CollectLatestFromFlow(flow, action = action)
            }

            flow.emit(1)

            composeTestRule.runOnIdle { action = { second++ } }
            composeTestRule.waitForIdle()

            flow.emit(1)

            assertEquals(1, first)
            assertEquals(1, second)
        }

    // ------------------------------------------------------------
    // OnChange
    // ------------------------------------------------------------

    @Test
    fun `OnChange triggers callback for initial value`() {
        var count = 0

        composeTestRule.setContent {
            OnChange(value = "Hello") { count++ }
        }

        assertEquals(1, count)
    }

    @Test
    fun `OnChange triggers only when value changes`() {
        var count = 0
        var value by mutableStateOf("A")

        composeTestRule.setContent {
            OnChange(value) { count++ }
        }

        composeTestRule.runOnIdle { value = "B" }
        composeTestRule.runOnIdle { value = "C" }
        composeTestRule.waitForIdle()

        assertEquals(3, count) // A → B → C
    }

    @Test
    fun `OnChange updates callback`() {
        var first = 0
        var second = 0

        var callback: suspend (String) -> Unit by mutableStateOf({ first++ })
        var value by mutableStateOf("Initial")

        composeTestRule.setContent {
            OnChange(value, callback = callback)
        }

        composeTestRule.runOnIdle {
            callback = { second++ }
            value = "Updated"
        }
        composeTestRule.waitForIdle()

        assertEquals(1, first)
        assertEquals(1, second)
    }

    @Test
    fun `OnChange restarts when keys change`() {
        var count = 0
        var value by mutableStateOf("A")
        var key by mutableStateOf(0)

        composeTestRule.setContent {
            OnChange(value, key1 = key) { count++ }
        }

        composeTestRule.runOnIdle { key = 1 }
        composeTestRule.runOnIdle { key = 2 }
        composeTestRule.waitForIdle()

        assertEquals(3, count) // initial + 2 relaunches
    }
}
