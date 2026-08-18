package com.w2sv.composed.foundation.layout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Adds vertical space of [height].
 * Shorthand for ′Spacer(Modifier.height(height))′.
 */
@Composable
@NonRestartableComposable
fun VerticalSpacer(height: Dp) {
    Spacer(Modifier.height(height))
}

/**
 * Adds horizontal space of [width].
 * Shorthand for ′Spacer(Modifier.width(width))′.
 */
@Composable
@NonRestartableComposable
fun HorizontalSpacer(width: Dp) {
    Spacer(Modifier.width(width))
}
