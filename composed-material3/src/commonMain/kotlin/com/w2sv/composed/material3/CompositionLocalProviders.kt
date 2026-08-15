package com.w2sv.composed.material3

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Provides [color] as [LocalContentColor] to the [content].
 */
@Composable
fun WithLocalContentColor(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides color, content)
}

/**
 * Lets Material components measure at their visual size without enforcing a
 * minimum interactive component size.
 */
@Composable
fun WithoutMinimumInteractiveComponentSize(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp,
        content = content
    )
}
