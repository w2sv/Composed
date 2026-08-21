package com.w2sv.composed.ui.layout

import kotlin.test.Test
import kotlin.test.assertContentEquals

class AnimatedSpacingColumnMeasurePolicyTest {

    @Test
    fun `spacing is placed between fully present items`() {
        assertSpacings(
            expected = intArrayOf(0, 10, 10),
            presence = floatArrayOf(1f, 1f, 1f)
        )
    }

    @Test
    fun `spacing is split around an absent middle item`() {
        assertSpacings(
            expected = intArrayOf(0, 5, 5),
            presence = floatArrayOf(1f, 0f, 1f)
        )
    }

    @Test
    fun `spacing is split symmetrically while a middle item transitions`() {
        assertSpacings(
            expected = intArrayOf(0, 6, 6),
            presence = floatArrayOf(1f, 0.5f, 1f),
            spacing = 8
        )
    }

    @Test
    fun `spacing bridges adjacent absent items`() {
        assertSpacings(
            expected = intArrayOf(0, 5, 0, 5),
            presence = floatArrayOf(1f, 0f, 0f, 1f)
        )
    }

    @Test
    fun `spacing follows the remaining edge items`() {
        assertSpacings(
            expected = intArrayOf(0, 0, 10),
            presence = floatArrayOf(0f, 1f, 1f)
        )
        assertSpacings(
            expected = intArrayOf(0, 10, 0),
            presence = floatArrayOf(1f, 1f, 0f)
        )
    }

    @Test
    fun `spacing clamps presence and handles degenerate inputs`() {
        assertSpacings(
            expected = intArrayOf(0, 0, 10),
            presence = floatArrayOf(-1f, 2f, 1f)
        )
        assertSpacings(
            expected = intArrayOf(),
            presence = floatArrayOf()
        )
        assertSpacings(
            expected = intArrayOf(0),
            presence = floatArrayOf(0.5f)
        )
        assertSpacings(
            expected = intArrayOf(0, 0),
            presence = floatArrayOf(1f, 1f),
            spacing = 0
        )
    }

    @Test
    fun `fully present weighted items divide available space by weight`() {
        assertAllocations(
            expected = intArrayOf(25, 75),
            weights = arrayOf(1f, 3f),
            presence = floatArrayOf(1f, 1f)
        )
    }

    @Test
    fun `disappearing weighted item gives its space to present sibling`() {
        assertAllocations(
            expected = intArrayOf(25, 75),
            weights = arrayOf(1f, 1f),
            presence = floatArrayOf(0.5f, 1f)
        )
        assertAllocations(
            expected = intArrayOf(0, 100),
            weights = arrayOf(1f, 3f),
            presence = floatArrayOf(0f, 1f)
        )
    }

    @Test
    fun `simultaneously disappearing weighted items also collapse flexible space`() {
        assertAllocations(
            expected = intArrayOf(38, 37),
            weights = arrayOf(1f, 1f),
            presence = floatArrayOf(0.5f, 0.5f)
        )
        assertAllocations(
            expected = intArrayOf(0, 0),
            weights = arrayOf(1f, 1f),
            presence = floatArrayOf(0f, 0f)
        )
    }

    @Test
    fun `non-weighted items do not receive redistributed space`() {
        assertAllocations(
            expected = intArrayOf(25, 0, 75),
            weights = arrayOf(1f, null, 1f),
            presence = floatArrayOf(0.5f, 1f, 1f)
        )
    }

    @Test
    fun `weighted allocation clamps presence`() {
        assertAllocations(
            expected = intArrayOf(0, 100),
            weights = arrayOf(1f, 1f),
            presence = floatArrayOf(-1f, 2f)
        )
    }

    @Test
    fun `weighted allocation distributes rounding remainder from the start`() {
        assertAllocations(
            expected = intArrayOf(4, 3, 3),
            weights = arrayOf(1f, 1f, 1f),
            presence = floatArrayOf(1f, 1f, 1f),
            availableSpace = 10
        )
    }

    @Test
    fun `weighted allocation handles no available space or weights`() {
        assertAllocations(
            expected = intArrayOf(0, 0),
            weights = arrayOf(1f, 1f),
            presence = floatArrayOf(1f, 1f),
            availableSpace = 0
        )
        assertAllocations(
            expected = intArrayOf(0, 0),
            weights = arrayOf(null, null),
            presence = floatArrayOf(1f, 1f)
        )
    }

    private fun assertSpacings(
        expected: IntArray,
        presence: FloatArray,
        spacing: Int = 10
    ) {
        assertContentEquals(
            expected = expected,
            actual = calculateSpacings(
                presence = presence,
                spacing = spacing
            )
        )
    }

    private fun assertAllocations(
        expected: IntArray,
        weights: Array<Float?>,
        presence: FloatArray,
        availableSpace: Int = 100
    ) {
        assertContentEquals(
            expected = expected,
            actual = calculateWeightedAllocations(
                availableSpace = availableSpace,
                parentData = Array(weights.size) { index ->
                    weights[index]?.let { weight ->
                        AnimatedSpacingColumnParentData(weight = weight)
                    }
                },
                presence = presence
            )
        )
    }
}
