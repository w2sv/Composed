package com.w2sv.composed.ui.layout.animatedspacing

import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class SymmetricSpacingTest {

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

    @Test
    fun `rounded gaps preserve the intended total spacing`() {
        val cases = listOf(
            floatArrayOf(1f, 0.1f, 1f),
            floatArrayOf(0.2f, 0.7f, 0.4f, 1f),
            floatArrayOf(1f, 0.25f, 0.5f, 0.75f, 1f),
            floatArrayOf(-1f, 0.3f, 2f, 0.6f, 0f, 1f)
        )

        cases.forEach { presence ->
            for (spacing in 1..17) {
                val expected = ((presence.sumOf { it.coerceIn(0f, 1f).toDouble() } - 1.0).coerceAtLeast(0.0) * spacing).roundToInt()
                assertEquals(
                    expected,
                    calculateSymmetricSpacings(presence, spacing).sum(),
                    "presence=${presence.contentToString()}, spacing=$spacing"
                )
            }
        }
    }

    @Test
    fun `rounding follows reversed presence in reverse order`() {
        val cases = listOf(
            floatArrayOf(1f, 0.2f, 0.8f, 1f),
            floatArrayOf(0.1f, 1f, 0.4f, 0.7f, 1f),
            floatArrayOf(1f, 0f, 0.5f, 1f, 0.25f, 1f)
        )

        cases.forEach { presence ->
            for (spacing in 1..17) {
                assertContentEquals(
                    calculateSymmetricSpacings(presence, spacing).reversedGaps(),
                    calculateSymmetricSpacings(presence.reversedArray(), spacing),
                    "presence=${presence.contentToString()}, spacing=$spacing"
                )
            }
        }
    }

    private fun assertSpacings(
        expected: IntArray,
        presence: FloatArray,
        spacing: Int = 10
    ) {
        assertContentEquals(expected, calculateSymmetricSpacings(presence, spacing))
    }

    private fun IntArray.reversedGaps(): IntArray =
        IntArray(size) { index -> if (index == 0) 0 else this[size - index] }
}
