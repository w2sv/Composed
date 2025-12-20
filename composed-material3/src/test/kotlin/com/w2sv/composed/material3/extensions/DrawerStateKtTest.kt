package com.w2sv.composed.material3.extensions

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DrawerStateKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun visibilityProgress() {
        val drawerState = DrawerState(initialValue = DrawerValue.Closed)
        lateinit var visibilityProgress: State<Float>
        val sheetWidth = 300.dp

        with(composeTestRule) {
            setContent {
                visibilityProgress = drawerState.rememberVisibilityProgress(sheetWidth)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = { ModalDrawerSheet(modifier = Modifier.width(sheetWidth)) {} },
                    content = {}
                )
            }

            runOnIdle {
                assertEquals(0f, visibilityProgress.value)

                runBlocking { drawerState.snapTo(DrawerValue.Open) }
                assertEquals(1f, visibilityProgress.value)
            }
        }
    }
}
