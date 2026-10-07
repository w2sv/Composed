package com.w2sv.composed.ui.systembar

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

/**
 * Applies the requested icon appearance to the Android status and navigation bars.
 *
 * @param useDarkStatusBarIcons whether the status bar should use dark icons.
 * @param useDarkNavigationBarIcons whether the navigation bar should use dark icons.
 */
@Composable
fun SystemBarIconAppearance(useDarkStatusBarIcons: Boolean, useDarkNavigationBarIcons: Boolean = useDarkStatusBarIcons) {
    val activity = LocalActivity.current ?: return

    SideEffect {
        val controller = WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        )
        controller.isAppearanceLightStatusBars = useDarkStatusBarIcons
        controller.isAppearanceLightNavigationBars = useDarkNavigationBarIcons
    }
}
