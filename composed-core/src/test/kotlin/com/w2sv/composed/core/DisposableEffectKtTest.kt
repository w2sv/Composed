package com.w2sv.composed.core

import androidx.activity.ComponentActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DisposableEffectKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `OnRemoveFromComposition basic functionality`() {
        var callbackTriggerCount = 0
        var removeFromComposition by mutableStateOf(false)

        composeTestRule.setContent {
            if (!removeFromComposition) {
                OnDispose {
                    callbackTriggerCount += 1
                }
            }

            LaunchedEffect(Unit) {
                removeFromComposition = true
            }
        }

        assertEquals(1, callbackTriggerCount)
    }

    @Test
    fun `OnRemoveFromComposition updates callback`() {
        var originalCallbackTriggerCount = 0
        var changedCallbackTriggerCount = 0

        val originalCallback = {
            originalCallbackTriggerCount += 1
        }
        val changedCallback = {
            changedCallbackTriggerCount += 1
        }

        var removeFromComposition by mutableStateOf(false)
        var callback by mutableStateOf(originalCallback)

        composeTestRule.setContent {
            LaunchedEffect(callback) {
                if (callback.hashCode() == changedCallback.hashCode()) {
                    removeFromComposition = true
                }
            }

            if (!removeFromComposition) {
                OnDispose(callback = callback)
            }

            LaunchedEffect(Unit) {
                callback = changedCallback
            }
        }

        assertEquals(0, originalCallbackTriggerCount)
        assertEquals(1, changedCallbackTriggerCount)
    }

    @Test
    fun `onLifecycleEvent basic functionality`() {
        var triggerCount = 0

        composeTestRule.setContent {
            OnLifecycleEvent(
                callback = { triggerCount += 1 },
                lifecycleEvent = Lifecycle.Event.ON_DESTROY
            )
        }
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        assertEquals(1, triggerCount)
    }

    @Test
    fun `onLifecycleEvent updates event`() {
        var receivedTriggerState: String? = null
        var triggerEvent by mutableStateOf(Lifecycle.Event.ON_START)

        composeTestRule.setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            OnLifecycleEvent(
                callback = {
                    receivedTriggerState = lifecycleOwner.lifecycle.currentState.toString()
                },
                lifecycleEvent = triggerEvent
            )
            LaunchedEffect(Unit) {
                triggerEvent = Lifecycle.Event.ON_DESTROY
            }
        }
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        assertEquals("DESTROYED", receivedTriggerState)
    }
}
