package com.w2sv.composeutils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SaverKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContentAndEmulateSavedInstanceStateRestore(content: @Composable () -> Unit) {
        StateRestorationTester(composeTestRule)
            .apply {
                setContent(content)
                emulateSavedInstanceStateRestore()
            }
    }

    @Test
    fun `colorSaver should correctly save and restore colors`() {
        setContentAndEmulateSavedInstanceStateRestore {
            val red by rememberSaveable(stateSaver = colorSaver()) {
                mutableStateOf(Color.Red)
            }
            Text(text = red.toString(), modifier = Modifier.testTag("colorText"))
        }
        composeTestRule
            .onNodeWithTag("colorText")
            .assert(hasTextExactly(Color.Red.toString()))
    }

    @Test
    fun `nullableColorSaver should correctly save and restore colors`() {
        setContentAndEmulateSavedInstanceStateRestore {
            val red by rememberSaveable(stateSaver = nullableColorSaver()) {
                mutableStateOf(Color.Red)
            }
            val nullColor by rememberSaveable(stateSaver = nullableColorSaver()) {
                mutableStateOf(null)
            }
            Text(text = red.toString(), modifier = Modifier.testTag("colorText"))
            Text(
                text = nullColor.toString(),
                modifier = Modifier.testTag("nullColorText")
            )
        }
        composeTestRule
            .onNodeWithTag("colorText")
            .assert(hasTextExactly(Color.Red.toString()))
        composeTestRule
            .onNodeWithTag("nullColorText")
            .assert(hasTextExactly("null"))
    }

    @Test
    fun `nullableListSaver should correctly save and restore colors`() {
        val nullableColorListSaver = nullableListSaver(
            saveNonNull = {
                listOf(it.toArgb())
            },
            restoreNonNull = {
                Color(it.first())
            }
        )
        setContentAndEmulateSavedInstanceStateRestore {
            val red by rememberSaveable(
                stateSaver = nullableColorListSaver
            ) {
                mutableStateOf(Color.Red)
            }
            Text(text = red.toString(), modifier = Modifier.testTag("colorText"))
            val nullColor by rememberSaveable(stateSaver = nullableColorListSaver) {
                mutableStateOf(null)
            }
            Text(
                text = nullColor.toString(),
                modifier = Modifier.testTag("nullColorText")
            )
        }
        composeTestRule
            .onNodeWithTag("colorText")
            .assert(hasTextExactly(Color.Red.toString()))
        composeTestRule
            .onNodeWithTag("nullColorText")
            .assert(hasTextExactly("null"))
    }
}