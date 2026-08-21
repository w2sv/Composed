package com.w2sv.composed.ui.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
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
class VisibilityMeasurePolicyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `presence scales measured height while retaining width`() {
        composeTestRule.setContent {
            VisibilityLayout(presence = 0.5f) {
                Box(Modifier.requiredSize(width = 40.dp, height = 100.dp))
            }
        }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertWidthIsEqualTo(40.dp)
            .assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `presence is clamped to its valid range`() {
        val presence = mutableStateOf(-1f)

        composeTestRule.setContent {
            VisibilityLayout(presence = presence.value) {
                Box(Modifier.requiredSize(width = 40.dp, height = 100.dp))
            }
        }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertHeightIsEqualTo(0.dp)

        composeTestRule.runOnIdle { presence.value = 2f }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `multiple children are overlaid using their maximum dimensions`() {
        composeTestRule.setContent {
            VisibilityLayout(presence = 0.5f) {
                Box(Modifier.requiredSize(width = 40.dp, height = 100.dp))
                Box(Modifier.requiredSize(width = 80.dp, height = 60.dp))
            }
        }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertWidthIsEqualTo(80.dp)
            .assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `fill weighted space expands child before applying presence`() {
        composeTestRule.setContent {
            VisibilityLayout(
                presence = 0.5f,
                fillWeightedSpace = true,
                modifier = Modifier
                    .widthIn(max = 100.dp)
                    .heightIn(max = 100.dp)
            ) {
                Box(Modifier.size(20.dp))
            }
        }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertWidthIsEqualTo(20.dp)
            .assertHeightIsEqualTo(50.dp)
    }

    @Test
    fun `non-filling weighted space preserves child preferred height`() {
        composeTestRule.setContent {
            VisibilityLayout(
                presence = 0.5f,
                fillWeightedSpace = false,
                modifier = Modifier
                    .widthIn(max = 100.dp)
                    .heightIn(max = 100.dp)
            ) {
                Box(Modifier.size(20.dp))
            }
        }

        composeTestRule
            .onNodeWithTag(VISIBILITY_TAG)
            .assertWidthIsEqualTo(20.dp)
            .assertHeightIsEqualTo(10.dp)
    }
}

@Composable
private fun VisibilityLayout(
    presence: Float,
    modifier: Modifier = Modifier,
    fillWeightedSpace: Boolean = false,
    content: @Composable () -> Unit
) {
    @SuppressLint("UnrememberedMutableState")
    val presenceState = mutableStateOf(presence)

    Layout(
        content = content,
        modifier = modifier.testTag(VISIBILITY_TAG),
        measurePolicy = VisibilityMeasurePolicy(
            presence = presenceState,
            fillWeightedSpace = fillWeightedSpace
        )
    )
}

private const val VISIBILITY_TAG = "visibility"
