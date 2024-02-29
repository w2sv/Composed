package com.w2sv.composeutils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

val isLandscapeModeActivated: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

val isPortraitModeActivated: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT