package com.w2sv.composed.extensions

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.properties.Delegates

@RunWith(RobolectricTestRunner::class)
class DrawerStateKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var maxWidthPx by Delegates.notNull<Float>()

    @Test
    fun visibilityPercentage() = runTest {
        val drawerState = DrawerState(initialValue = DrawerValue.Open)

        composeTestRule.setContent {
            maxWidthPx = DrawerDefaults.MaximumDrawerWidth.toPx()
            ModalNavigationDrawer(
                drawerContent = { /*TODO*/ },
                drawerState = drawerState,
                content = {}
            )
        }

        val visibilityPercentage by drawerState.visibilityPercentage(maxWidthPx)

        assertEquals(1f, visibilityPercentage)

        drawerState.snapTo(DrawerValue.Closed)

        assertEquals(0f, visibilityPercentage)
    }
}