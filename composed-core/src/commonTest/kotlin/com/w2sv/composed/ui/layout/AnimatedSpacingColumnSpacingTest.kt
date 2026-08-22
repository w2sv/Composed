package com.w2sv.composed.ui.layout

import kotlin.test.Test
import kotlin.test.assertContentEquals

class AnimatedSpacingColumnSpacingTest {

    @Test
    fun `spacing is placed between fully present items`() {
        assertSpacings(intArrayOf(0, 10, 10), floatArrayOf(1f, 1f, 1f))
    }

    @Test
    fun `spacing is split around an absent middle item`() {
        assertSpacings(intArrayOf(0, 5, 5), floatArrayOf(1f, 0f, 1f))
    }

    @Test
    fun `spacing is split symmetrically while a middle item transitions`() {
        assertSpacings(intArrayOf(0, 6, 6), floatArrayOf(1f, 0.5f, 1f), spacing = 8)
    }

    @Test
    fun `spacing bridges adjacent absent items`() {
        assertSpacings(intArrayOf(0, 5, 0, 5), floatArrayOf(1f, 0f, 0f, 1f))
    }

    @Test
    fun `spacing follows the remaining edge items`() {
        assertSpacings(intArrayOf(0, 0, 10), floatArrayOf(0f, 1f, 1f))
        assertSpacings(intArrayOf(0, 10, 0), floatArrayOf(1f, 1f, 0f))
    }

    @Test
    fun `spacing clamps presence and handles degenerate inputs`() {
        assertSpacings(intArrayOf(0, 0, 10), floatArrayOf(-1f, 2f, 1f))
        assertSpacings(intArrayOf(), floatArrayOf())
        assertSpacings(intArrayOf(0), floatArrayOf(0.5f))
        assertSpacings(intArrayOf(0, 0), floatArrayOf(1f, 1f), spacing = 0)
    }

    private fun assertSpacings(
        expected: IntArray,
        presence: FloatArray,
        spacing: Int = 10
    ) {
        assertContentEquals(expected, calculateSpacings(presence, spacing))
    }
}
