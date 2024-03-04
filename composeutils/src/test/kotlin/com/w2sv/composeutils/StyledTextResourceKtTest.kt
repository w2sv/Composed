package com.w2sv.composeutils

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.AnnotatedString
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
            val boldText = styledTextResource(id = R.string.bold_text)
            boldText.examine()

            val italicText = styledTextResource(id = R.string.bold_text)
            italicText.examine()
        }
    }
}

private fun AnnotatedString.examine() {
    println(this)
    println(spanStyles)
}