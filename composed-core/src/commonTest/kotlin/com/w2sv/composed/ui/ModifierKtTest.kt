package com.w2sv.composed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ModifierKtTest {

    @Test
    fun `then should append produced Modifier`() {
        assertEquals(
            Modifier.padding(8.dp).background(Color.Red),
            Modifier
                .padding(8.dp)
                .then {
                    background(Color.Red)
                }
        )
    }

    @Test
    fun `then should build appended Modifier from empty Modifier`() {
        assertEquals(
            Modifier
                .padding(8.dp)
                .background(Color.Red),
            Modifier
                .padding(8.dp)
                .then {
                    background(Color.Red)
                }
        )
    }

    @Test
    fun `then should invoke block exactly once`() {
        var invocationCount = 0

        Modifier.then {
            invocationCount++
            padding(8.dp)
        }

        assertEquals(1, invocationCount)
    }

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
    fun `thenIf should preserve receiving Modifier when condition is false`() {
        assertEquals(
            Modifier.padding(8.dp),
            Modifier
                .padding(8.dp)
                .thenIf(false) {
                    background(Color.Red)
                }
        )
    }

    @Test
    fun `thenIf should apply onTrue Modifier when condition is true with both branches`() {
        assertEquals(
            Modifier.padding(8.dp),
            Modifier.thenIf(
                condition = true,
                onTrue = { padding(8.dp) },
                onFalse = { background(Color.Red) }
            )
        )
    }

    @Test
    fun `thenIf should apply onFalse Modifier when condition is false`() {
        assertEquals(
            Modifier.background(Color.Red),
            Modifier.thenIf(
                condition = false,
                onTrue = { padding(8.dp) },
                onFalse = { background(Color.Red) }
            )
        )
    }

    @Test
    fun `thenIf should apply selected branch to receiving Modifier`() {
        assertEquals(
            Modifier
                .padding(8.dp)
                .background(Color.Red),
            Modifier
                .padding(8.dp)
                .thenIf(
                    condition = false,
                    onTrue = { padding(16.dp) },
                    onFalse = { background(Color.Red) }
                )
        )
    }

    @Test
    fun `thenIf should invoke only onTrue when condition is true`() {
        var onTrueInvocations = 0
        var onFalseInvocations = 0

        Modifier.thenIf(
            condition = true,
            onTrue = {
                onTrueInvocations++
                this
            },
            onFalse = {
                onFalseInvocations++
                this
            }
        )

        assertEquals(1, onTrueInvocations)
        assertEquals(0, onFalseInvocations)
    }

    @Test
    fun `thenIf should invoke only onFalse when condition is false`() {
        var onTrueInvocations = 0
        var onFalseInvocations = 0

        Modifier.thenIf(
            condition = false,
            onTrue = {
                onTrueInvocations++
                this
            },
            onFalse = {
                onFalseInvocations++
                this
            }
        )

        assertEquals(0, onTrueInvocations)
        assertEquals(1, onFalseInvocations)
    }

    @Test
    fun `thenIf should chain multiple modifiers`() {
        assertEquals(
            Modifier
                .padding(8.dp)
                .background(Color.Green),
            Modifier.thenIf(true) {
                padding(8.dp)
                    .thenIf(true) {
                        background(Color.Green)
                    }
            }
        )
    }

    @Test
    fun `thenIfNotNull should apply Modifier when value is not null`() {
        assertEquals(
            Modifier.padding(16.dp),
            Modifier.thenIfNotNull(16.dp) { padding ->
                padding(padding)
            }
        )
    }

    @Test
    fun `thenIfNotNull should pass value to Modifier block`() {
        val color = Color.Red

        assertEquals(
            Modifier.background(color),
            Modifier.thenIfNotNull(color) {
                background(it)
            }
        )
    }

    @Test
    fun `thenIfNotNull should preserve receiving Modifier when value is null`() {
        assertEquals(
            Modifier.padding(8.dp),
            Modifier
                .padding(8.dp)
                .thenIfNotNull<Color>(null) {
                    background(it)
                }
        )
    }

    @Test
    fun `thenIfNotNull should apply Modifier to receiving Modifier`() {
        assertEquals(
            Modifier
                .padding(8.dp)
                .background(Color.Red),
            Modifier
                .padding(8.dp)
                .thenIfNotNull(Color.Red) {
                    background(it)
                }
        )
    }

    @Test
    fun `thenIfNotNull should invoke block exactly once for non-null value`() {
        var invocationCount = 0

        Modifier.thenIfNotNull(Color.Red) {
            invocationCount++
            background(it)
        }

        assertEquals(1, invocationCount)
    }

    @Test
    fun `thenIfNotNull should not invoke block for null value`() {
        var invocationCount = 0

        Modifier.thenIfNotNull<Color>(null) {
            invocationCount++
            background(it)
        }

        assertEquals(0, invocationCount)
    }
}
