package com.w2sv.composed.ui.layout

import kotlin.math.roundToInt
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
    fun `optimized spacing matches symmetric directional calculation`() {
        val presenceValues = floatArrayOf(-1f, 0f, 0.25f, 0.5f, 1f, 2f)

        presenceValues.forEach { first ->
            presenceValues.forEach { second ->
                presenceValues.forEach { third ->
                    presenceValues.forEach { fourth ->
                        val presence = floatArrayOf(first, second, third, fourth)

                        listOf(1, 7, 10).forEach { spacing ->
                            assertContentEquals(
                                expected = calculateSpacingsReference(presence, spacing),
                                actual = calculateSpacings(presence, spacing)
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `fully present weighted items divide available space by weight`() {
        assertOrdinaryAllocations(
            expected = intArrayOf(25, 75),
            weights = arrayOf(1f, 3f)
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
        assertOrdinaryAllocations(
            expected = intArrayOf(4, 3, 3),
            weights = arrayOf(1f, 1f, 1f),
            availableSpace = 10
        )
    }

    @Test
    fun `weighted allocation handles no available space or weights`() {
        assertOrdinaryAllocations(
            expected = intArrayOf(0, 0),
            weights = arrayOf(1f, 1f),
            availableSpace = 0
        )
        assertOrdinaryAllocations(
            expected = intArrayOf(0, 0),
            weights = arrayOf(null, null)
        )
    }

    @Test
    fun `ordinary weight fast path matches all-present animated allocation`() {
        val weightSets = listOf(
            arrayOf<Float?>(1f),
            arrayOf(1f, 1f),
            arrayOf(1f, 3f),
            arrayOf(1f, null, 2f, 4f),
            arrayOf<Float?>(null, null)
        )

        weightSets.forEach { weights ->
            val parentData = Array(weights.size) { index ->
                weights[index]?.let { weight ->
                    AnimatedSpacingColumnParentData(weight = weight)
                }
            }

            listOf(0, 1, 10, 99, 100).forEach { availableSpace ->
                assertContentEquals(
                    expected = calculateAnimatedWeightedAllocations(
                        availableSpace = availableSpace,
                        parentData = parentData,
                        presence = FloatArray(weights.size) { 1f }
                    ),
                    actual = calculateOrdinaryWeightedAllocations(
                        availableSpace = availableSpace,
                        parentData = parentData
                    )
                )
            }
        }
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
            actual = calculateAnimatedWeightedAllocations(
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

    private fun assertOrdinaryAllocations(
        expected: IntArray,
        weights: Array<Float?>,
        availableSpace: Int = 100
    ) {
        assertContentEquals(
            expected = expected,
            actual = calculateOrdinaryWeightedAllocations(
                availableSpace = availableSpace,
                parentData = Array(weights.size) { index ->
                    weights[index]?.let { weight ->
                        AnimatedSpacingColumnParentData(weight = weight)
                    }
                }
            )
        )
    }

    private fun calculateSpacingsReference(presence: FloatArray, spacing: Int): IntArray {
        if (presence.size <= 1 || spacing == 0) {
            return IntArray(presence.size)
        }

        val forward = calculateDirectionalSpacingsReference(presence, reversed = false)
        val reverse = calculateDirectionalSpacingsReference(presence, reversed = true)

        return IntArray(presence.size) { index ->
            if (index == 0) {
                0
            } else {
                (spacing * (forward[index] + reverse[index - 1]) / 2f).roundToInt()
            }
        }
    }

    private fun calculateDirectionalSpacingsReference(presence: FloatArray, reversed: Boolean): FloatArray {
        val result = FloatArray(presence.size)
        var accumulatedPresence = 0f
        var previousGapCount = 0f

        repeat(presence.size) { iteration ->
            val index = if (reversed) presence.lastIndex - iteration else iteration

            accumulatedPresence += presence[index].coerceIn(0f, 1f)

            val gapCount = (accumulatedPresence - 1f).coerceAtLeast(0f)
            result[index] = gapCount - previousGapCount
            previousGapCount = gapCount
        }

        return result
    }
}
