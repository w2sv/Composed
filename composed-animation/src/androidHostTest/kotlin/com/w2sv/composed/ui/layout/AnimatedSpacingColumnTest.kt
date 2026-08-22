package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.VerticalAlignmentLine
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
class AnimatedSpacingColumnTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `weight fills remaining bounded height after spacing`() {
        composeTestRule.setContent {
            AnimatedSpacingColumn(
                spacing = 10.dp,
                modifier = Modifier
                    .size(width = 100.dp, height = 100.dp)
                    .testTag(COLUMN_TAG)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .testTag(FIXED_TAG)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .testTag(WEIGHTED_TAG)
                )
            }
        }

        composeTestRule
            .onNodeWithTag(WEIGHTED_TAG)
            .assertHeightIsEqualTo(70.dp)
            .assertTopPositionInRootIsEqualTo(30.dp)
    }

    @Test
    fun `ordinary weight rounding matches stock Column`() {
        composeTestRule.setContent {
            Row {
                Column(Modifier.size(width = 10.dp, height = 5.dp)) {
                    Spacer(Modifier.weight(1f).testTag("stock-first"))
                    Spacer(Modifier.weight(1f).testTag("stock-second"))
                }
                AnimatedSpacingColumn(0.dp, Modifier.size(width = 10.dp, height = 5.dp)) {
                    Spacer(Modifier.weight(1f).testTag("animated-first"))
                    Spacer(Modifier.weight(1f).testTag("animated-second"))
                }
            }
        }

        composeTestRule.onNodeWithTag("stock-first").assertHeightIsEqualTo(2.dp)
        composeTestRule.onNodeWithTag("animated-first").assertHeightIsEqualTo(2.dp)
        composeTestRule.onNodeWithTag("stock-second").assertHeightIsEqualTo(3.dp)
        composeTestRule.onNodeWithTag("animated-second").assertHeightIsEqualTo(3.dp)
    }

    @Test
    fun `default and child horizontal alignments are applied`() {
        composeTestRule.setContent {
            AnimatedSpacingColumn(
                spacing = 0.dp,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 100.dp, height = 40.dp)
                    .testTag(COLUMN_TAG)
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .testTag(CENTERED_TAG)
                )
                Box(
                    Modifier
                        .size(20.dp)
                        .align(Alignment.End)
                        .testTag(END_TAG)
                )
            }
        }

        composeTestRule
            .onNodeWithTag(CENTERED_TAG)
            .assertLeftPositionInRootIsEqualTo(40.dp)
        composeTestRule
            .onNodeWithTag(END_TAG)
            .assertLeftPositionInRootIsEqualTo(80.dp)
    }

    @Test
    fun `alignBy aligns child alignment lines and expands column width`() {
        composeTestRule.setContent {
            AnimatedSpacingColumn(
                spacing = 0.dp,
                modifier = Modifier.testTag(COLUMN_TAG)
            ) {
                AlignmentLineBox(
                    width = 20,
                    linePosition = 5,
                    modifier = Modifier
                        .alignBy(TestAlignmentLine)
                        .testTag(FIRST_ALIGNED_TAG)
                )
                AlignmentLineBox(
                    width = 30,
                    linePosition = 10,
                    modifier = Modifier
                        .alignBy(TestAlignmentLine)
                        .testTag(SECOND_ALIGNED_TAG)
                )
            }
        }

        composeTestRule
            .onNodeWithTag(COLUMN_TAG)
            .assertWidthIsEqualTo(30.dp)
        composeTestRule
            .onNodeWithTag(FIRST_ALIGNED_TAG)
            .assertLeftPositionInRootIsEqualTo(5.dp)
        composeTestRule
            .onNodeWithTag(SECOND_ALIGNED_TAG)
            .assertLeftPositionInRootIsEqualTo(0.dp)
    }

    @Test
    fun `visibility change collapses content and recomputes spacing`() {
        var middleVisible by mutableStateOf(true)

        composeTestRule.setContent {
            AnimatedSpacingColumn(
                spacing = 10.dp,
                modifier = Modifier.testTag(COLUMN_TAG)
            ) {
                Item(FIRST_TAG)
                AnimatedVisibility(
                    visible = middleVisible,
                    animationSpec = snap(),
                    fade = false
                ) {
                    Item(MIDDLE_TAG)
                }
                Item(LAST_TAG)
            }
        }

        composeTestRule
            .onNodeWithTag(COLUMN_TAG)
            .assertHeightIsEqualTo(80.dp)
        composeTestRule
            .onNodeWithTag(LAST_TAG)
            .assertTopPositionInRootIsEqualTo(60.dp)

        composeTestRule.runOnIdle { middleVisible = false }
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(COLUMN_TAG)
            .assertHeightIsEqualTo(50.dp)
        composeTestRule
            .onNodeWithTag(LAST_TAG)
            .assertTopPositionInRootIsEqualTo(30.dp)
    }

    @Composable
    private fun Item(tag: String) {
        Box(
            Modifier
                .size(width = 20.dp, height = 20.dp)
                .testTag(tag)
        )
    }

    @Composable
    private fun AlignmentLineBox(
        width: Int,
        linePosition: Int,
        modifier: Modifier = Modifier
    ) {
        Layout(
            content = {},
            modifier = modifier,
            measurePolicy = { _, _ ->
                layout(
                    width = width,
                    height = 10,
                    alignmentLines = mapOf(TestAlignmentLine to linePosition)
                ) {}
            }
        )
    }

    private companion object {
        val TestAlignmentLine = VerticalAlignmentLine(::minOf)

        const val COLUMN_TAG = "column"
        const val FIXED_TAG = "fixed"
        const val WEIGHTED_TAG = "weighted"
        const val CENTERED_TAG = "centered"
        const val END_TAG = "end"
        const val FIRST_ALIGNED_TAG = "first-aligned"
        const val SECOND_ALIGNED_TAG = "second-aligned"
        const val FIRST_TAG = "first"
        const val MIDDLE_TAG = "middle"
        const val LAST_TAG = "last"
    }
}
