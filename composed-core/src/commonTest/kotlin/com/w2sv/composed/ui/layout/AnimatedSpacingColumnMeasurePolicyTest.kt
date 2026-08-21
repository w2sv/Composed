package com.w2sv.composed.ui.layout

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AnimatedSpacingColumnMeasurePolicyTest {

    @Test
    fun `measure returns minimum constraints for empty content`() {
        val result = measure(
            measurables = emptyList(),
            constraints = Constraints(
                minWidth = 10,
                maxWidth = 100,
                minHeight = 20,
                maxHeight = 200
            )
        )

        assertEquals(10, result.width)
        assertEquals(20, result.height)
    }

    @Test
    fun `measure constrains fixed children by remaining bounded height`() {
        val first = TestMeasurable(preferredWidth = 30, preferredHeight = 70)
        val second = TestMeasurable(preferredWidth = 40, preferredHeight = 70)

        val result = measure(
            measurables = listOf(first, second),
            constraints = Constraints(maxWidth = 100, maxHeight = 100),
            spacing = 10
        )
        result.placeChildren()

        assertEquals(40, result.width)
        assertEquals(100, result.height)
        assertEquals(90, first.measuredConstraints.maxHeight)
        assertEquals(20, second.measuredConstraints.maxHeight)
        assertEquals(IntSize(30, 70), first.placeable.size)
        assertEquals(IntSize(40, 20), second.placeable.size)
        assertEquals(IntOffset(0, 0), first.placeable.position)
        assertEquals(IntOffset(0, 80), second.placeable.position)
    }

    @Test
    fun `measure allocates ordinary weight after fixed content and spacing`() {
        val fixed = TestMeasurable(preferredWidth = 20, preferredHeight = 20)
        val weighted = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 0,
            parentData = AnimatedSpacingColumnParentData(weight = 1f)
        )

        val result = measure(
            measurables = listOf(fixed, weighted),
            constraints = Constraints(maxWidth = 100, maxHeight = 100),
            spacing = 10
        )
        result.placeChildren()

        assertEquals(100, result.height)
        assertEquals(70, weighted.measuredConstraints.minHeight)
        assertEquals(70, weighted.measuredConstraints.maxHeight)
        assertEquals(IntSize(20, 70), weighted.placeable.size)
        assertEquals(IntOffset(0, 30), weighted.placeable.position)
    }

    @Test
    fun `measure lets non-filling weight keep preferred height`() {
        val weighted = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 30,
            parentData = AnimatedSpacingColumnParentData(
                weight = 1f,
                fill = false
            )
        )

        val result = measure(
            measurables = listOf(weighted),
            constraints = Constraints(maxWidth = 100, maxHeight = 100)
        )

        assertEquals(30, result.height)
        assertEquals(0, weighted.measuredConstraints.minHeight)
        assertEquals(100, weighted.measuredConstraints.maxHeight)
    }

    @Test
    fun `measure ignores weight under unbounded height`() {
        val weighted = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 30,
            parentData = AnimatedSpacingColumnParentData(weight = 1f)
        )

        val result = measure(
            measurables = listOf(weighted),
            constraints = Constraints(maxWidth = 100)
        )

        assertEquals(30, result.height)
        assertEquals(0, weighted.measuredConstraints.minHeight)
        assertEquals(Constraints.Infinity, weighted.measuredConstraints.maxHeight)
    }

    @Test
    fun `measure compensates animated weighted allocation for presence`() {
        val animated = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 50,
            parentData = AnimatedSpacingColumnParentData(
                weight = 1f,
                presence = mutableStateOf(0.5f),
                visibilityControlled = true
            ),
            size = { constraints ->
                IntSize(20, constraints.maxHeight / 2)
            }
        )
        val ordinary = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 0,
            parentData = AnimatedSpacingColumnParentData(weight = 1f)
        )

        val result = measure(
            measurables = listOf(animated, ordinary),
            constraints = Constraints(maxWidth = 100, maxHeight = 100)
        )

        assertEquals(100, result.height)
        assertEquals(50, animated.measuredConstraints.maxHeight)
        assertEquals(IntSize(20, 25), animated.placeable.size)
        assertEquals(75, ordinary.measuredConstraints.minHeight)
        assertEquals(75, ordinary.measuredConstraints.maxHeight)
    }

    @Test
    fun `measure applies default and child horizontal alignment`() {
        val centered = TestMeasurable(preferredWidth = 20, preferredHeight = 10)
        val endAligned = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 10,
            parentData = AnimatedSpacingColumnParentData(
                crossAxisAlignment = CrossAxisAlignment.Horizontal(Alignment.End)
            )
        )

        val result = measure(
            measurables = listOf(centered, endAligned),
            constraints = Constraints(minWidth = 100, maxWidth = 100, maxHeight = 100),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        result.placeChildren()

        assertEquals(IntOffset(40, 0), centered.placeable.position)
        assertEquals(IntOffset(80, 10), endAligned.placeable.position)
    }

    @Test
    fun `measure places start alignment on the right in RTL`() {
        val child = TestMeasurable(preferredWidth = 20, preferredHeight = 10)

        val result = measure(
            measurables = listOf(child),
            constraints = Constraints(minWidth = 100, maxWidth = 100, maxHeight = 100),
            horizontalAlignment = Alignment.Start,
            layoutDirection = LayoutDirection.Rtl
        )
        result.placeChildren()

        assertEquals(IntOffset(80, 0), child.placeable.position)
    }

    @Test
    fun `measure aligns relative children and handles unspecified lines`() {
        val first = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 10,
            parentData = relativeParentData(TestAlignmentLine),
            alignmentLines = mapOf(TestAlignmentLine to 5)
        )
        val second = TestMeasurable(
            preferredWidth = 30,
            preferredHeight = 10,
            parentData = relativeParentData(TestAlignmentLine),
            alignmentLines = mapOf(TestAlignmentLine to 10)
        )
        val unspecified = TestMeasurable(
            preferredWidth = 15,
            preferredHeight = 10,
            parentData = relativeParentData(TestAlignmentLine)
        )

        val result = measure(
            measurables = listOf(first, second, unspecified),
            constraints = Constraints(maxWidth = 100, maxHeight = 100)
        )
        result.placeChildren()

        assertEquals(30, result.width)
        assertEquals(IntOffset(5, 0), first.placeable.position)
        assertEquals(IntOffset(0, 10), second.placeable.position)
        assertEquals(IntOffset(0, 20), unspecified.placeable.position)
    }

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

private fun measure(
    measurables: List<Measurable>,
    constraints: Constraints,
    spacing: Int = 0,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
): MeasureResult {
    val policy = AnimatedSpacingColumnMeasurePolicy(
        spacing = spacing.dp,
        horizontalAlignment = horizontalAlignment
    )
    val scope = TestMeasureScope(layoutDirection)

    return with(policy) {
        scope.measure(measurables, constraints)
    }
}

private fun relativeParentData(alignmentLine: VerticalAlignmentLine) =
    AnimatedSpacingColumnParentData(
        crossAxisAlignment = CrossAxisAlignment.Relative(
            AlignmentLineProvider.Value(alignmentLine)
        )
    )

private class TestMeasureScope(override val layoutDirection: LayoutDirection) : MeasureScope {
    override val density: Float = 1f
    override val fontScale: Float = 1f
}

private class TestMeasurable(
    private val preferredWidth: Int,
    private val preferredHeight: Int,
    override val parentData: Any? = null,
    private val alignmentLines: Map<AlignmentLine, Int> = emptyMap(),
    private val size: ((Constraints) -> IntSize)? = null
) : Measurable {

    private var recordedConstraints: Constraints? = null

    val measuredConstraints: Constraints
        get() = requireNotNull(recordedConstraints)

    lateinit var placeable: TestPlaceable
        private set

    override fun measure(constraints: Constraints): Placeable {
        recordedConstraints = constraints

        val measuredSize = size?.invoke(constraints) ?: IntSize(
            width = constraints.constrainWidth(preferredWidth),
            height = constraints.constrainHeight(preferredHeight)
        )

        return TestPlaceable(
            measuredSize = measuredSize,
            constraints = constraints,
            alignmentLines = alignmentLines
        ).also { placeable = it }
    }

    override fun minIntrinsicWidth(height: Int): Int =
        preferredWidth

    override fun maxIntrinsicWidth(height: Int): Int =
        preferredWidth

    override fun minIntrinsicHeight(width: Int): Int =
        preferredHeight

    override fun maxIntrinsicHeight(width: Int): Int =
        preferredHeight
}

private class TestPlaceable(measuredSize: IntSize, constraints: Constraints, private val alignmentLines: Map<AlignmentLine, Int>) :
    Placeable() {

    val size: IntSize = measuredSize
    var position: IntOffset? = null
        private set

    init {
        measurementConstraints = constraints
        this.measuredSize = measuredSize
    }

    override fun get(alignmentLine: AlignmentLine): Int =
        alignmentLines[alignmentLine] ?: AlignmentLine.Unspecified

    override fun placeAt(
        position: IntOffset,
        zIndex: Float,
        layerBlock: (GraphicsLayerScope.() -> Unit)?
    ) {
        this.position = position
    }
}

private val TestAlignmentLine = VerticalAlignmentLine(::minOf)
