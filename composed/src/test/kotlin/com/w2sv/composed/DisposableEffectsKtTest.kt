package com.w2sv.composed

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.delay
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DisposableEffectsKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onRemoveFromComposition() {
        var callbackTriggerCount = 0
        var removeFromComposition by mutableStateOf(false)

        composeTestRule.setContent {
            println("Composing")

            if (!removeFromComposition) {
                OnRemoveFromComposition {
                    println("Running callback")
                    callbackTriggerCount += 1
                }
            }

            LaunchedEffect(Unit) {
                println("Removing")
                removeFromComposition = true
                println("Removed")
            }
        }

        assertEquals(1, callbackTriggerCount)
    }

    @Test
    fun `OnRemoveFromComposition updates callback`() {
        var originalCallbackTriggerCount = 0
        var changedCallbackTriggerCount = 0

        var removeFromComposition by mutableStateOf(false)
        var callback by mutableStateOf(
            {
                println("Original callback triggered")
                originalCallbackTriggerCount += 1
            }
        )

        composeTestRule.setContent {
            println("Composing")

            LaunchedEffect(callback.hashCode()) {
                println(callback.hashCode())
            }

            if (!removeFromComposition) {
                OnRemoveFromComposition(callback = callback)
            }

            LaunchedEffect(Unit) {
                callback = {
                    println("Changed callback triggered")
                    changedCallbackTriggerCount += 1
                }
                println("Changed callback")
                delay(100)
                removeFromComposition = true
                println("Removed from composition")
            }
        }

        composeTestRule.mainClock.advanceTimeBy(100)

        assertEquals(0, originalCallbackTriggerCount)
        assertEquals(1, changedCallbackTriggerCount)
    }
}