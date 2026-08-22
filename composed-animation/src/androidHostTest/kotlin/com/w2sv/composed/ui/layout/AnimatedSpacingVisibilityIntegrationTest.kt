package com.w2sv.composed.ui.layout

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@OptIn(ExperimentalAnimatedSpacingApi::class)
class AnimatedSpacingVisibilityIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun stopAutomaticClockAdvancement() {
        composeTestRule.mainClock.autoAdvance = false
    }

    @Test
    fun `enter and exit animate sibling position and retain content until completion`() {
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            AnimatedSpacingColumn(spacing = 10.dp) {
                Item("first")
                AnimatedVisibility(visible, animationSpec = AnimationSpec, fade = false) {
                    Item("content")
                }
                Item("last")
            }
        }

        startTransition { visible = false }
        advanceHalfway()
        assertContentCount(1)
        composeTestRule.onNodeWithTag("last").assertTopPositionInRootIsEqualTo(46.dp)

        completeTransition()
        assertContentCount(0)
        composeTestRule.onNodeWithTag("last").assertTopPositionInRootIsEqualTo(30.dp)

        startTransition { visible = true }
        advanceHalfway()
        assertContentCount(1)
        composeTestRule.onNodeWithTag("last").assertTopPositionInRootIsEqualTo(44.dp)

        completeTransition()
        composeTestRule.onNodeWithTag("last").assertTopPositionInRootIsEqualTo(60.dp)
    }

    @Test
    fun `exit can rapidly reverse without disposing content`() {
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            AnimatedSpacingColumn(spacing = 10.dp) {
                Item("first")
                AnimatedVisibility(visible, animationSpec = AnimationSpec, fade = false) { Item("content") }
                Item("last")
            }
        }

        startTransition { visible = false }
        composeTestRule.mainClock.advanceTimeBy(300)
        startTransition { visible = true }
        composeTestRule.mainClock.advanceTimeBy(1_000, ignoreFrameDuration = true)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()

        assertContentCount(1)
        composeTestRule.onNodeWithTag("last").assertTopPositionInRootIsEqualTo(60.dp)
    }

    @Test
    fun `one animated weighted child releases occupied space to its sibling`() {
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp, Modifier.size(width = 20.dp, height = 100.dp)) {
                AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.weight(1f).testTag("animated"),
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(Modifier.fillMaxSize())
                }
                Spacer(Modifier.weight(1f).testTag("ordinary"))
            }
        }

        startTransition { visible = false }
        advanceHalfway()

        composeTestRule.onNodeWithTag("animated").assertHeightIsEqualTo(27.dp)
        composeTestRule.onNodeWithTag("ordinary").assertHeightIsEqualTo(73.dp)
    }

    @Test
    fun `multiple animated weighted children realize simultaneous allocations`() {
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp, Modifier.size(width = 20.dp, height = 100.dp)) {
                AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.weight(1f).testTag("first-weighted"),
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(Modifier.fillMaxSize())
                }
                AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.weight(1f).testTag("second-weighted"),
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(Modifier.fillMaxSize())
                }
            }
        }

        startTransition { visible = false }
        advanceHalfway()

        composeTestRule.onNodeWithTag("first-weighted").assertHeightIsEqualTo(39.dp)
        composeTestRule.onNodeWithTag("second-weighted").assertHeightIsEqualTo(39.dp)
    }

    @Test
    fun `column reveal and collapse use their configured structural anchors`() {
        var entering by mutableStateOf(false)
        var exiting by mutableStateOf(true)
        var enteringWrapperTop = 0f
        var enteringContentTop = 0f
        var exitingWrapperTop = 0f
        var exitingContentTop = 0f
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp) {
                AnimatedVisibility(
                    visible = entering,
                    modifier = Modifier.onGloballyPositioned { enteringWrapperTop = it.positionInRoot().y },
                    expandFrom = Alignment.Bottom,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .onGloballyPositioned { enteringContentTop = it.positionInRoot().y }
                    )
                }
                AnimatedVisibility(
                    visible = exiting,
                    modifier = Modifier.onGloballyPositioned { exitingWrapperTop = it.positionInRoot().y },
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .onGloballyPositioned { exitingContentTop = it.positionInRoot().y }
                    )
                }
            }
        }

        startTransition {
            entering = true
            exiting = false
        }
        advanceHalfway()
        composeTestRule.runOnIdle {
            assertEquals(-11f, enteringContentTop - enteringWrapperTop, 0.01f)
            assertEquals(-9f, exitingContentTop - exitingWrapperTop, 0.01f)
        }
    }

    @Test
    fun `row logical anchors resolve in RTL for reveal and collapse`() {
        var visible by mutableStateOf(false)
        var wrapperLeft = 0f
        var contentLeft = 0f
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AnimatedSpacingRow(0.dp) {
                    AnimatedVisibility(
                        visible = visible,
                        modifier = Modifier.onGloballyPositioned { wrapperLeft = it.positionInRoot().x },
                        expandFrom = Alignment.Start,
                        shrinkTowards = Alignment.End,
                        animationSpec = AnimationSpec,
                        fade = false
                    ) {
                        Box(
                            Modifier
                                .size(20.dp)
                                .onGloballyPositioned { contentLeft = it.positionInRoot().x }
                        )
                    }
                }
            }
        }

        startTransition { visible = true }
        advanceHalfway()
        composeTestRule.runOnIdle { assertEquals(-11f, contentLeft - wrapperLeft, 0.01f) }

        finishEnterAndStartExit { visible = false }
        advanceHalfway()
        composeTestRule.runOnIdle { assertEquals(0f, contentLeft - wrapperLeft, 0.01f) }
    }

    private fun advanceHalfway() {
        composeTestRule.mainClock.advanceTimeBy(500, ignoreFrameDuration = true)
        composeTestRule.waitForIdle()
    }

    private fun finishEnterAndStartExit(startExit: () -> Unit) {
        finishTransition()
        startTransition(startExit)
    }

    private fun finishTransition() {
        composeTestRule.mainClock.advanceTimeBy(500, ignoreFrameDuration = true)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
    }

    private fun completeTransition() {
        composeTestRule.mainClock.advanceTimeBy(1_000, ignoreFrameDuration = true)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
    }

    private fun startTransition(changeTarget: () -> Unit) {
        composeTestRule.runOnIdle(changeTarget)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.waitForIdle()
    }

    private fun assertContentCount(expected: Int) {
        assertEquals(expected, composeTestRule.onAllNodesWithTag("content").fetchSemanticsNodes().size)
    }

    @Composable
    private fun Item(tag: String) {
        Box(Modifier.size(20.dp).testTag(tag))
    }

    private companion object {
        val AnimationSpec = tween<Float>(durationMillis = 1_000, easing = LinearEasing)
    }
}
