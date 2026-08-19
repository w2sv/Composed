package com.w2sv.composed.animation

import androidx.compose.foundation.lazy.grid.LazyGridState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LazyGridItemEntranceStateTest {
    @Test
    fun `reset with a key forgets only that entered key`() {
        val state = LazyGridItemEntranceState(LazyGridState())
        state.markEntered("first")
        state.markEntered("second")

        state.reset("first")

        assertFalse(state.hasEntered("first"))
        assertTrue(state.hasEntered("second"))
    }

    @Test
    fun `reset without a key forgets all entered keys`() {
        val state = LazyGridItemEntranceState(LazyGridState())
        state.markEntered("first")
        state.markEntered("second")

        state.reset()

        assertFalse(state.hasEntered("first"))
        assertFalse(state.hasEntered("second"))
    }

    @Test
    fun `reset treats the next entrances as an initial layout until scrolling starts`() {
        val state = LazyGridItemEntranceState(LazyGridState())
        val detectedContext = LazyGridItemEntranceScrollContext.AfterForwardScroll

        state.reset()

        assertEquals(LazyGridItemEntranceScrollContext.InitialLayout, state.scrollContext(detectedContext))

        state.onScrollStarted()

        assertEquals(detectedContext, state.scrollContext(detectedContext))
    }
}
