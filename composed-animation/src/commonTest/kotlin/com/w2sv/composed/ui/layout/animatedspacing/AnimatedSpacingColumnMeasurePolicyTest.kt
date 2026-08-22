package com.w2sv.composed.ui.layout.animatedspacing

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedSpacingColumnMeasurePolicyTest {

    @Test
    fun `measure returns minimum constraints for empty content`() {
        val result = measureColumn(
            measurables = emptyList(),
            constraints = Constraints(minWidth = 10, maxWidth = 100, minHeight = 20, maxHeight = 200)
        )

        assertEquals(10, result.width)
        assertEquals(20, result.height)
    }

    @Test
    fun `measure constrains fixed children by remaining bounded height`() {
        val first = TestMeasurable(preferredWidth = 30, preferredHeight = 70)
        val second = TestMeasurable(preferredWidth = 40, preferredHeight = 70)

        val result = measureColumn(
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

        val result = measureColumn(
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
            parentData = AnimatedSpacingColumnParentData(weight = 1f, fill = false)
        )

        val result = measureColumn(
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

        val result = measureColumn(
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
            size = { constraints -> IntSize(20, constraints.maxHeight / 2) }
        )
        val ordinary = TestMeasurable(
            preferredWidth = 20,
            preferredHeight = 0,
            parentData = AnimatedSpacingColumnParentData(weight = 1f)
        )

        val result = measureColumn(
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

        val result = measureColumn(
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

        val result = measureColumn(
            measurables = listOf(child),
            constraints = Constraints(minWidth = 100, maxWidth = 100, maxHeight = 100),
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

        val result = measureColumn(
            measurables = listOf(first, second, unspecified),
            constraints = Constraints(maxWidth = 100, maxHeight = 100)
        )
        result.placeChildren()

        assertEquals(30, result.width)
        assertEquals(IntOffset(5, 0), first.placeable.position)
        assertEquals(IntOffset(0, 10), second.placeable.position)
        assertEquals(IntOffset(0, 20), unspecified.placeable.position)
    }
}
