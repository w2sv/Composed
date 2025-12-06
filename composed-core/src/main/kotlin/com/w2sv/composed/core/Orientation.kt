package com.w2sv.composed.core

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * @return `true` when the current configuration is in landscape orientation.
 */
val isLandscapeModeActive: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * @return `true` when the current configuration is in portrait orientation.
 */
val isPortraitModeActive: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
