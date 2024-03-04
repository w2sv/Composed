package com.w2sv.composeutils

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StyledTextResourceKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun conversion() {
        composeTestRule.setContent {
            testStyledText(id = R.string.bold,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.fontWeight == FontWeight(weight = 700) }
            )
            testStyledText(id = R.string.italic,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.fontStyle == FontStyle.Italic }
            )
            testStyledText(id = R.string.bold_italic_consecutive,
                expectedSpanStyleCount = 2,
                checkFirstSpanStyle = { it.fontWeight == FontWeight(weight = 700) }
            )
            testStyledText(id = R.string.bold_italic_nested,
                expectedSpanStyleCount = 4,
                checkFirstSpanStyle = { it.fontWeight == FontWeight(weight = 700) }
            )
            testStyledText(
                id = R.string.subscript,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.baselineShift == BaselineShift(multiplier = -0.5f) }
            )
            testStyledText(
                id = R.string.superscript,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.baselineShift == BaselineShift(multiplier = 0.5f) }
            )
//            testStyledText(id = R.string.strikethrough)
            testStyledText(
                id = R.string.underline,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.textDecoration == TextDecoration.Underline }
            )
            testStyledText(id = R.string.colored,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.color == Color(0.6f, 0.0f, 1.0f, 1.0f) }
            )
            testStyledText(id = R.string.monospace,
                expectedSpanStyleCount = 1,
                checkFirstSpanStyle = { it.fontFamily == FontFamily.Monospace }
            )
        }
    }
}

@SuppressLint("ComposeNamingUppercase", "ComposableNaming")
@Composable
private fun testStyledText(
    @StringRes id: Int,
    expectedSpanStyleCount: Int,
    checkFirstSpanStyle: (SpanStyle) -> Boolean
) {
    val styled = rememberStyledTextResource(id = id)
    assertEquals(stringResource(id = id), styled.toString())
    println(styled.spanStyles)
    assertEquals(expectedSpanStyleCount, styled.spanStyles.size)
    assertTrue(checkFirstSpanStyle(styled.spanStyles.first().item))
}