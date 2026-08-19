package com.w2sv.composed.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LazyGridItemEntranceNodeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `once per key is marked before delay and reset allows another entrance`() {
        var showItem by mutableStateOf(true)
        lateinit var entranceState: LazyGridItemEntranceState
        val delayComputations = AtomicInteger()
        val nonCompletingDelay = LazyGridItemEntranceDelay {
            delayComputations.incrementAndGet()
            Duration.INFINITE
        }

        composeTestRule.setContent {
            val gridState = rememberLazyGridState()
            entranceState = rememberLazyGridItemEntranceState(gridState)

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                state = gridState,
                modifier = Modifier.size(100.dp)
            ) {
                if (showItem) {
                    item(key = ITEM_KEY) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .animateLazyGridItemEntrance(
                                    itemKey = ITEM_KEY,
                                    state = entranceState,
                                    delay = nonCompletingDelay
                                )
                        )
                    }
                }
            }
        }

        composeTestRule.waitUntil { delayComputations.get() == 1 }
        recomposeItem(show = false) { showItem = it }
        recomposeItem(show = true) { showItem = it }

        assertEquals(1, delayComputations.get())

        recomposeItem(show = false) { showItem = it }
        composeTestRule.runOnIdle { entranceState.reset(ITEM_KEY) }
        recomposeItem(show = true) { showItem = it }
        composeTestRule.waitUntil { delayComputations.get() == 2 }
    }

    @Test
    fun `on composition runs again when the item reenters composition`() {
        var showItem by mutableStateOf(true)
        val delayComputations = AtomicInteger()
        val nonCompletingDelay = LazyGridItemEntranceDelay {
            delayComputations.incrementAndGet()
            Duration.INFINITE
        }

        composeTestRule.setContent {
            val gridState = rememberLazyGridState()
            val entranceState = rememberLazyGridItemEntranceState(gridState)

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                state = gridState,
                modifier = Modifier.size(100.dp)
            ) {
                if (showItem) {
                    item(key = ITEM_KEY) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .animateLazyGridItemEntrance(
                                    itemKey = ITEM_KEY,
                                    state = entranceState,
                                    repeatMode = LazyGridItemEntranceRepeatMode.OnComposition,
                                    delay = nonCompletingDelay
                                )
                        )
                    }
                }
            }
        }

        composeTestRule.waitUntil { delayComputations.get() == 1 }
        recomposeItem(show = false) { showItem = it }
        recomposeItem(show = true) { showItem = it }
        composeTestRule.waitUntil { delayComputations.get() == 2 }
    }

    @Test
    fun `delay scopes use orientation relative main and cross axis offsets`() {
        val verticalScopes = mutableMapOf<Int, LazyGridItemEntranceDelayScope>()
        val horizontalScopes = mutableMapOf<Int, LazyGridItemEntranceDelayScope>()

        composeTestRule.setContent {
            val verticalState = rememberLazyGridState()
            val verticalEntranceState = rememberLazyGridItemEntranceState(verticalState)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = verticalState,
                modifier = Modifier.size(100.dp)
            ) {
                items(count = 4, key = { it }) { index ->
                    Box(
                        Modifier
                            .size(40.dp)
                            .animateLazyGridItemEntrance(
                                itemKey = index,
                                state = verticalEntranceState,
                                delay = { scope ->
                                    verticalScopes[index] = scope
                                    Duration.ZERO
                                }
                            )
                    )
                }
            }

            val horizontalState = rememberLazyGridState()
            val horizontalEntranceState = rememberLazyGridItemEntranceState(horizontalState)
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                state = horizontalState,
                modifier = Modifier.size(100.dp)
            ) {
                items(count = 4, key = { it }) { index ->
                    Box(
                        Modifier
                            .size(40.dp)
                            .animateLazyGridItemEntrance(
                                itemKey = index,
                                state = horizontalEntranceState,
                                delay = { scope ->
                                    horizontalScopes[index] = scope
                                    Duration.ZERO
                                }
                            )
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.runOnIdle {
            assertEquals(4, verticalScopes.size)
            assertEquals(4, horizontalScopes.size)

            with(verticalScopes.getValue(1)) {
                assertEquals(0, row)
                assertEquals(1, column)
                assertEquals(0, mainAxisOffset)
                assertEquals(1, crossAxisOffset)
            }
            with(horizontalScopes.getValue(1)) {
                assertEquals(1, row)
                assertEquals(0, column)
                assertEquals(0, mainAxisOffset)
                assertEquals(1, crossAxisOffset)
            }
        }
    }

    private fun recomposeItem(show: Boolean, update: (Boolean) -> Unit) {
        composeTestRule.runOnIdle { update(show) }
        composeTestRule.waitForIdle()
    }

    private companion object {
        const val ITEM_KEY = "item"
    }
}
