package com.w2sv.composed.playground.shared

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.font.FontWeight

val Typography.controlLabel
    @ReadOnlyComposable
    @Composable
    get() = bodyMedium.copy(fontWeight = FontWeight.SemiBold)
