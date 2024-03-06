package com.w2sv.composed.extensions

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
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