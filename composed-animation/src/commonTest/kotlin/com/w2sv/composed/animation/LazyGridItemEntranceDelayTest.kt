package com.w2sv.composed.animation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds

class LazyGridItemEntranceDelayTest {
    @Test
    fun `axis strategies use their corresponding offsets`() {
        val scope = scope(mainAxisOffset = 2, crossAxisOffset = 3)

        assertEquals(
            expected = 400.milliseconds,
            actual = LazyGridItemEntranceDelay.alongMainAxis(200.milliseconds).compute(scope)
        )
        assertEquals(
            expected = 450.milliseconds,
            actual = LazyGridItemEntranceDelay.alongCrossAxis(150.milliseconds).compute(scope)
        )
    }

    @Test
    fun `sequential strategy uses the flattened item offset`() {
        assertEquals(
            expected = 350.milliseconds,
            actual = LazyGridItemEntranceDelay.sequential(50.milliseconds)
                .compute(scope(itemOffset = 7))
        )
    }

    @Test
    fun `diagonal strategy combines main and cross axis delays`() {
        assertEquals(
            expected = 850.milliseconds,
            actual = LazyGridItemEntranceDelay.diagonal(
                mainAxisInterval = 200.milliseconds,
                crossAxisInterval = 150.milliseconds
            ).compute(scope(mainAxisOffset = 2, crossAxisOffset = 3))
        )
    }

    @Test
    fun `built in strategies reject negative intervals`() {
        assertFailsWith<IllegalArgumentException> {
            LazyGridItemEntranceDelay.alongMainAxis((-1).milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            LazyGridItemEntranceDelay.alongCrossAxis((-1).milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            LazyGridItemEntranceDelay.sequential((-1).milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            LazyGridItemEntranceDelay.diagonal(mainAxisInterval = (-1).milliseconds)
        }
        assertFailsWith<IllegalArgumentException> {
            LazyGridItemEntranceDelay.diagonal(crossAxisInterval = (-1).milliseconds)
        }
    }

    private fun scope(
        mainAxisOffset: Int = 0,
        crossAxisOffset: Int = 0,
        itemOffset: Int = 0
    ) =
        LazyGridItemEntranceDelayScope(
            itemIndex = 0,
            row = 0,
            column = 0,
            mainAxisOffset = mainAxisOffset,
            crossAxisOffset = crossAxisOffset,
            itemOffset = itemOffset,
            scrollContext = LazyGridItemEntranceScrollContext.InitialLayout
        )
}
