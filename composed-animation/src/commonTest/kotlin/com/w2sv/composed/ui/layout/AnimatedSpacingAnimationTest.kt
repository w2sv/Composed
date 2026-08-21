package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.spring
import androidx.compose.ui.Alignment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalAnimatedSpacingApi::class)
class AnimatedSpacingAnimationTest {

    @Test
    fun `column animation retains existing defaults`() {
        val animation = AnimatedSpacingColumnAnimation()

        assertEquals(spring<Float>(), animation.animationSpec)
        assertTrue(animation.fade)
        assertEquals(Alignment.Top, animation.expandFrom)
        assertEquals(Alignment.Top, animation.shrinkTowards)
    }

    @Test
    fun `row animation retains existing defaults`() {
        val animation = AnimatedSpacingRowAnimation()

        assertEquals(spring<Float>(), animation.animationSpec)
        assertTrue(animation.fade)
        assertEquals(Alignment.Start, animation.expandFrom)
        assertEquals(Alignment.Start, animation.shrinkTowards)
    }
}
