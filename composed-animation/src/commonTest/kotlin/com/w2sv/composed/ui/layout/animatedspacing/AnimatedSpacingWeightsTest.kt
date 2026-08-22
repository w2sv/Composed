package com.w2sv.composed.ui.layout.animatedspacing

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AnimatedSpacingWeightsTest {

    @Test
    fun `fully present weighted items divide available space by weight`() {
        assertOrdinaryAllocations(intArrayOf(25, 75), arrayOf(1f, 3f))
    }

    @Test
    fun `disappearing weighted item gives its space to present sibling`() {
        assertAnimatedAllocations(intArrayOf(25, 75), arrayOf(1f, 1f), floatArrayOf(0.5f, 1f))
        assertAnimatedAllocations(intArrayOf(0, 100), arrayOf(1f, 3f), floatArrayOf(0f, 1f))
    }

    @Test
    fun `simultaneously disappearing weighted items also collapse flexible space`() {
        assertAnimatedAllocations(intArrayOf(38, 37), arrayOf(1f, 1f), floatArrayOf(0.5f, 0.5f))
        assertAnimatedAllocations(intArrayOf(0, 0), arrayOf(1f, 1f), floatArrayOf(0f, 0f))
    }

    @Test
    fun `non-weighted items do not receive redistributed space`() {
        assertAnimatedAllocations(intArrayOf(25, 0, 75), arrayOf(1f, null, 1f), floatArrayOf(0.5f, 1f, 1f))
    }

    @Test
    fun `weighted allocation clamps presence`() {
        assertAnimatedAllocations(intArrayOf(0, 100), arrayOf(1f, 1f), floatArrayOf(-1f, 2f))
    }

    @Test
    fun `weighted allocation distributes rounding remainder from the start`() {
        assertOrdinaryAllocations(intArrayOf(4, 3, 3), arrayOf(1f, 1f, 1f), availableSpace = 10)
        assertOrdinaryAllocations(intArrayOf(2, 3), arrayOf(1f, 1f), availableSpace = 5)
    }

    @Test
    fun `weighted allocation handles no available space or weights`() {
        assertOrdinaryAllocations(intArrayOf(0, 0), arrayOf(1f, 1f), availableSpace = 0)
        assertOrdinaryAllocations(intArrayOf(0, 0), arrayOf(null, null))
    }

    @Test
    fun `ordinary fast path preserves the available-space total`() {
        val weightSets: List<Array<Float?>> = listOf(
            arrayOf<Float?>(1f),
            arrayOf(1f, 1f),
            arrayOf(1f, 3f),
            arrayOf(1f, null, 2f, 4f),
            arrayOf<Float?>(null, null)
        )

        weightSets.forEach { weights ->
            val parentData = weights.toParentData()

            listOf(0, 1, 10, 99, 100).forEach { availableSpace ->
                val expected = if (weights.any { it != null }) availableSpace else 0
                assertEquals(expected, calculateFoundationWeightedAllocations(availableSpace, parentData).sum())
            }
        }
    }

    private fun assertAnimatedAllocations(
        expected: IntArray,
        weights: Array<Float?>,
        presence: FloatArray,
        availableSpace: Int = 100
    ) {
        assertContentEquals(
            expected,
            calculateAnimatedWeightedAllocations(availableSpace, weights.toParentData(), presence)
        )
    }

    private fun assertOrdinaryAllocations(
        expected: IntArray,
        weights: Array<Float?>,
        availableSpace: Int = 100
    ) {
        assertContentEquals(
            expected,
            calculateFoundationWeightedAllocations(availableSpace, weights.toParentData())
        )
    }

    private fun Array<Float?>.toParentData(): Array<AnimatedSpacingParentData?> =
        Array(size) { index -> this[index]?.let { AnimatedSpacingParentData(weight = it) } }
}
