package com.w2sv.composed.core.extensions

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.w2sv.composed.core.extensions.visibilityPercentage
import kotlin.properties.Delegates
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawerStateKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var maxWidthPx by Delegates.notNull<Float>()

    @Ignore("Mysteriously not working anymore after update of compose dependencies. DrawerState.currentOffset always null.")
    @Test
    fun visibilityPercentage() =
        runTest {
            val drawerState = DrawerState(initialValue = DrawerValue.Closed)

            composeTestRule.setContent {
                maxWidthPx = 120f
                ModalNavigationDrawer(
                    drawerContent = { /*TODO*/ },
                    drawerState = drawerState,
                    content = {}
                )
            }

            val visibilityPercentage by drawerState.visibilityPercentage(maxWidthPx)

            assertEquals(0f, visibilityPercentage)

            drawerState.snapTo(DrawerValue.Open)

            assertEquals(1f, visibilityPercentage)
        }
}
