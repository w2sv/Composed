package com.w2sv.composed.ui.graphics

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorKtTest {

    @Test
    fun `parse Color from String`() {
        assertEquals(
            Color.Red,
            Color.parse("#FFFF0000")
        )
        assertEquals(
            Color.Red,
            Color.parse("#FF0000")
        )
    }
}
