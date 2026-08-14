package com.w2sv.composed.core.extensions

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class StringKtTest {

    @Test
    fun toComposeColor() {
        assertEquals(
            Color.Red,
            "#FFFF0000".toComposeColor()
        )
        assertEquals(
            Color.Red,
            "#FF0000".toComposeColor()
        )
        assertEquals(Color.Red, "red".toComposeColor())
    }
}
