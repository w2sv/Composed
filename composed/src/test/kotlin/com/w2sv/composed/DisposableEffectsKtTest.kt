package com.w2sv.composed

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
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

        val originalCallback = {
            println("Original callback triggered")
            originalCallbackTriggerCount += 1
        }
        val changedCallback = {
            println("Changed callback triggered")
            changedCallbackTriggerCount += 1
        }

        var removeFromComposition by mutableStateOf(false)
        var callback by mutableStateOf(originalCallback)

        composeTestRule.setContent {
            println("Composing")

            LaunchedEffect(callback) {
                if (callback.hashCode() == changedCallback.hashCode()) {
                    removeFromComposition = true
                    println("Set removeFromComposition=true")
                }
            }

            if (!removeFromComposition) {
                OnRemoveFromComposition(callback = callback)
            }

            LaunchedEffect(Unit) {
                callback = changedCallback
                println("Changed callback")
            }
        }

        assertEquals(0, originalCallbackTriggerCount)
        assertEquals(1, changedCallbackTriggerCount)
    }
}