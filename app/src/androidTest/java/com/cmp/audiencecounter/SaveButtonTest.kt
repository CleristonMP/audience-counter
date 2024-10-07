package com.cmp.audiencecounter

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.SaveButton
import org.junit.Rule
import org.junit.Test

class SaveButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    @Test
    fun TestSaveButtonClick() {
        var wasClicked = false

        composeTestRule.setContent {
            SaveButton(
                onClick = { wasClicked = true },
                isSaving = false
            )
        }

        composeTestRule.onNodeWithText("Salvar").performClick()
        assert(wasClicked)
    }
}