package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalAnimatedSpacingApi::class)
class AnimatedSpacingRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `weight fills remaining bounded width after spacing`() {
        composeTestRule.setContent {
            AnimatedSpacingRow(10.dp, Modifier.size(100.dp, 40.dp).testTag("row")) {
                Box(Modifier.size(20.dp).testTag("fixed"))
                Spacer(Modifier.weight(1f).fillMaxHeight().testTag("weighted"))
            }
        }

        composeTestRule.onNodeWithTag("weighted")
            .assertWidthIsEqualTo(70.dp)
            .assertLeftPositionInRootIsEqualTo(30.dp)
    }

    @Test
    fun `default and child vertical alignments are applied`() {
        composeTestRule.setContent {
            AnimatedSpacingRow(0.dp, Modifier.size(100.dp, 40.dp), Alignment.CenterVertically) {
                Box(Modifier.size(20.dp).testTag("centered"))
                Box(Modifier.size(20.dp).align(Alignment.Bottom).testTag("bottom"))
            }
        }

        composeTestRule.onNodeWithTag("centered").assertTopPositionInRootIsEqualTo(10.dp)
        composeTestRule.onNodeWithTag("bottom").assertTopPositionInRootIsEqualTo(20.dp)
    }

    @Test
    fun `visibility change collapses width and recomputes spacing`() {
        var middleVisible by mutableStateOf(true)
        composeTestRule.setContent {
            AnimatedSpacingRow(10.dp, Modifier.testTag("row")) {
                Box(Modifier.size(20.dp))
                AnimatedVisibility(middleVisible, animationSpec = snap(), fade = false) {
                    Box(Modifier.size(20.dp).testTag("middle"))
                }
                Box(Modifier.size(20.dp).testTag("last"))
            }
        }

        composeTestRule.onNodeWithTag("row").assertWidthIsEqualTo(80.dp)
        composeTestRule.onNodeWithTag("last").assertLeftPositionInRootIsEqualTo(60.dp)

        composeTestRule.runOnIdle { middleVisible = false }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("row").assertWidthIsEqualTo(50.dp)
        composeTestRule.onNodeWithTag("last").assertLeftPositionInRootIsEqualTo(30.dp)
        composeTestRule.onNodeWithTag("row").assertHeightIsEqualTo(20.dp)
    }
}
