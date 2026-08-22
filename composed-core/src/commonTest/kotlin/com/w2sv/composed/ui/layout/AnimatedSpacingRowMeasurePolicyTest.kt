package com.w2sv.composed.ui.layout

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimatedSpacingRowMeasurePolicyTest {

    @Test
    fun `measure returns minimum constraints for empty content`() {
        val result = measureRow(emptyList(), Constraints(minWidth = 10, maxWidth = 100, minHeight = 20, maxHeight = 200))
        assertEquals(IntSize(10, 20), IntSize(result.width, result.height))
    }

    @Test
    fun `measure allocates ordinary weight after fixed content and spacing`() {
        val fixed = TestMeasurable(20, 20)
        val weighted = TestMeasurable(0, 20, AnimatedSpacingColumnParentData(weight = 1f))

        val result = measureRow(listOf(fixed, weighted), Constraints(maxWidth = 100, maxHeight = 100), spacing = 10)
        result.placeChildren()

        assertEquals(70, weighted.measuredConstraints.minWidth)
        assertEquals(IntSize(70, 20), weighted.placeable.size)
        assertEquals(IntOffset(30, 0), weighted.placeable.position)
    }

    @Test
    fun `measure ignores weight under unbounded width`() {
        val weighted = TestMeasurable(30, 20, AnimatedSpacingColumnParentData(weight = 1f))
        val result = measureRow(listOf(weighted), Constraints(maxHeight = 100))

        assertEquals(30, result.width)
        assertEquals(Constraints.Infinity, weighted.measuredConstraints.maxWidth)
    }

    @Test
    fun `measure compensates animated weighted allocation for presence`() {
        val animated = TestMeasurable(
            50,
            20,
            AnimatedSpacingColumnParentData(weight = 1f, presence = mutableStateOf(0.5f), visibilityControlled = true),
            size = { IntSize(it.maxWidth / 2, 20) }
        )
        val ordinary = TestMeasurable(0, 20, AnimatedSpacingColumnParentData(weight = 1f))

        val result = measureRow(listOf(animated, ordinary), Constraints(maxWidth = 100, maxHeight = 100))

        assertEquals(100, result.width)
        assertEquals(50, animated.measuredConstraints.maxWidth)
        assertEquals(75, ordinary.measuredConstraints.minWidth)
    }

    @Test
    fun `measure applies vertical alignment and alignment lines`() {
        val centered = TestMeasurable(10, 10)
        val bottom = TestMeasurable(
            10,
            10,
            AnimatedSpacingColumnParentData(crossAxisAlignment = CrossAxisAlignment.Vertical(Alignment.Bottom))
        )
        val first = TestMeasurable(10, 20, relativeParentData(TestHorizontalAlignmentLine), mapOf(TestHorizontalAlignmentLine to 5))
        val second = TestMeasurable(10, 30, relativeParentData(TestHorizontalAlignmentLine), mapOf(TestHorizontalAlignmentLine to 10))

        val result = measureRow(
            listOf(centered, bottom, first, second),
            Constraints(maxWidth = 100, minHeight = 40, maxHeight = 40),
            verticalAlignment = Alignment.CenterVertically
        )
        result.placeChildren()

        assertEquals(IntOffset(0, 15), centered.placeable.position)
        assertEquals(IntOffset(10, 30), bottom.placeable.position)
        assertEquals(IntOffset(20, 5), first.placeable.position)
        assertEquals(IntOffset(30, 0), second.placeable.position)
    }

    @Test
    fun `measure places first child on the right in RTL`() {
        val first = TestMeasurable(20, 10)
        val second = TestMeasurable(30, 10)
        val result = measureRow(
            listOf(first, second),
            Constraints(minWidth = 100, maxWidth = 100, maxHeight = 100),
            spacing = 10,
            layoutDirection = LayoutDirection.Rtl
        )
        result.placeChildren()

        assertEquals(IntOffset(80, 0), first.placeable.position)
        assertEquals(IntOffset(40, 0), second.placeable.position)
    }
}
