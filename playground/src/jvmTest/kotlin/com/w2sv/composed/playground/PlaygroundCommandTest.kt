package com.w2sv.composed.playground

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PlaygroundArgumentsTest {
    @Test
    fun `no arguments launches the sample picker`() {
        assertNull(parseInitialSample(emptyArray()))
    }

    @Test
    fun `sample ID selects its sample`() {
        assertEquals(Sample.LazyGridItemEntrance, parseInitialSample(arrayOf("lazy-grid-item-entrance")))
    }

    @Test
    fun `unknown sample is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            parseInitialSample(arrayOf("missing"))
        }
    }

    @Test
    fun `multiple sample IDs are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            parseInitialSample(arrayOf("shake", "lazy-grid-item-entrance"))
        }
    }
}
