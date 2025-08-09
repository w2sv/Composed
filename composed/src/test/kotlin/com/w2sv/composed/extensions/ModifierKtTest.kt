package com.w2sv.composed.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ModifierKtTest {

    @Test
    fun `thenIf should apply onTrue Modifier when condition is true`() {
        assertEquals(
            Modifier.padding(16.dp),
            Modifier.thenIf(true) {
                padding(16.dp)
            }
        )
    }

    @Test
    fun `thenIf should apply onFalse Modifier when condition is false`() {
        assertEquals(
            Modifier.background(color = Color.Red),
            Modifier.thenIf(
                false,
                onTrue = { padding(8.dp) },
                onFalse = { background(color = Color.Red) }
            )
        )
    }

    @Test
    fun `thenIf should chain multiple modifiers`() {
        assertEquals(
            Modifier
                .padding(8.dp)
                .background(color = Color.Green),
            Modifier.thenIf(true) {
                padding(8.dp)
                    .thenIf(condition = true) {
                        background(color = Color.Green)
                    }
            }
        )
    }
}
