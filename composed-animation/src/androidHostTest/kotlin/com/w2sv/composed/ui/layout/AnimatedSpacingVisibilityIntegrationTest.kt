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
        val enteringGeometry = AxisGeometry()
        val exitingGeometry = AxisGeometry()
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp) {
                AnimatedVisibility(
                    visible = entering,
                    modifier = Modifier.onGloballyPositioned { enteringGeometry.recordVerticalWrapper(it) },
                    expandFrom = Alignment.Bottom,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .onGloballyPositioned { enteringGeometry.recordVerticalContent(it) }
                    )
                }
                AnimatedVisibility(
                    visible = exiting,
                    modifier = Modifier.onGloballyPositioned { exitingGeometry.recordVerticalWrapper(it) },
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .onGloballyPositioned { exitingGeometry.recordVerticalContent(it) }
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
            enteringGeometry.assertVerticalAlignment(Alignment.Bottom)
            exitingGeometry.assertVerticalAlignment(Alignment.Bottom)
        }
    }

    @Test
    fun `row logical anchors resolve in RTL for reveal and collapse`() {
        var visible by mutableStateOf(false)
        val geometry = AxisGeometry()
        composeTestRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AnimatedSpacingRow(0.dp) {
                    AnimatedVisibility(
                        visible = visible,
                        modifier = Modifier.onGloballyPositioned { geometry.recordHorizontalWrapper(it) },
                        expandFrom = Alignment.Start,
                        shrinkTowards = Alignment.End,
                        animationSpec = AnimationSpec,
                        fade = false
                    ) {
                        Box(
                            Modifier
                                .size(20.dp)
                                .onGloballyPositioned { geometry.recordHorizontalContent(it) }
                        )
                    }
                }
            }
        }

        startTransition { visible = true }
        advanceHalfway()
        composeTestRule.runOnIdle { geometry.assertHorizontalAlignment(Alignment.Start, LayoutDirection.Rtl) }

        finishEnterAndStartExit { visible = false }
        advanceHalfway()
        composeTestRule.runOnIdle { geometry.assertHorizontalAlignment(Alignment.End, LayoutDirection.Rtl) }
    }

    @Test
    fun `reversing exit back to enter preserves its collapse anchor without a position jump`() {
        var visible by mutableStateOf(true)
        val geometry = AxisGeometry()
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp) {
                AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.onGloballyPositioned { geometry.recordVerticalWrapper(it) },
                    expandFrom = Alignment.Top,
                    shrinkTowards = Alignment.Bottom,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(Modifier.size(20.dp).onGloballyPositioned { geometry.recordVerticalContent(it) })
                }
            }
        }

        startTransition { visible = false }
        composeTestRule.mainClock.advanceTimeBy(300, ignoreFrameDuration = true)
        composeTestRule.waitForIdle()
        geometry.assertVerticalAlignment(Alignment.Bottom)
        val beforeReversal = geometry.snapshot()

        startTransition { visible = true }
        composeTestRule.runOnIdle {
            geometry.assertVerticalAlignment(Alignment.Bottom)
            geometry.assertNoAnchorSwitchSince(beforeReversal)
        }
    }

    @Test
    fun `reversing enter back to exit preserves its reveal anchor without a position jump`() {
        var visible by mutableStateOf(false)
        val geometry = AxisGeometry()
        composeTestRule.setContent {
            AnimatedSpacingColumn(0.dp) {
                AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.onGloballyPositioned { geometry.recordVerticalWrapper(it) },
                    expandFrom = Alignment.Bottom,
                    shrinkTowards = Alignment.Top,
                    animationSpec = AnimationSpec,
                    fade = false
                ) {
                    Box(Modifier.size(20.dp).onGloballyPositioned { geometry.recordVerticalContent(it) })
                }
            }
        }

        startTransition { visible = true }
        composeTestRule.mainClock.advanceTimeBy(300, ignoreFrameDuration = true)
        composeTestRule.waitForIdle()
        geometry.assertVerticalAlignment(Alignment.Bottom)
        val beforeReversal = geometry.snapshot()

        startTransition { visible = false }
        composeTestRule.runOnIdle {
            geometry.assertVerticalAlignment(Alignment.Bottom)
            geometry.assertNoAnchorSwitchSince(beforeReversal)
        }
    }

    private fun advanceHalfway() {
        composeTestRule.mainClock.advanceTimeBy(500, ignoreFrameDuration = true)
        composeTestRule.waitForIdle()
    }

    private fun finishEnterAndStartExit(startExit: () -> Unit) {
        completeTransition()
        startTransition(startExit)
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

private class AxisGeometry {
    private var wrapperPosition = 0f
    private var contentPosition = 0f
    private var wrapperSize = 0
    private var contentSize = 0

    fun recordVerticalWrapper(coordinates: androidx.compose.ui.layout.LayoutCoordinates) {
        wrapperPosition = coordinates.positionInRoot().y
        wrapperSize = coordinates.size.height
    }

    fun recordVerticalContent(coordinates: androidx.compose.ui.layout.LayoutCoordinates) {
        contentPosition = coordinates.positionInRoot().y
        contentSize = coordinates.size.height
    }

    fun recordHorizontalWrapper(coordinates: androidx.compose.ui.layout.LayoutCoordinates) {
        wrapperPosition = coordinates.positionInRoot().x
        wrapperSize = coordinates.size.width
    }

    fun recordHorizontalContent(coordinates: androidx.compose.ui.layout.LayoutCoordinates) {
        contentPosition = coordinates.positionInRoot().x
        contentSize = coordinates.size.width
    }

    fun assertVerticalAlignment(alignment: Alignment.Vertical) {
        assertEquals(alignment.align(contentSize, wrapperSize).toFloat(), offset, 0.01f)
    }

    fun assertHorizontalAlignment(alignment: Alignment.Horizontal, layoutDirection: LayoutDirection) {
        assertEquals(alignment.align(contentSize, wrapperSize, layoutDirection).toFloat(), offset, 0.01f)
    }

    fun snapshot() =
        Snapshot(offset, wrapperSize)

    fun assertNoAnchorSwitchSince(previous: Snapshot) {
        assertEquals((wrapperSize - previous.wrapperSize).toFloat(), offset - previous.offset, 0.01f)
    }

    private val offset get() = contentPosition - wrapperPosition

    data class Snapshot(val offset: Float, val wrapperSize: Int)
}
