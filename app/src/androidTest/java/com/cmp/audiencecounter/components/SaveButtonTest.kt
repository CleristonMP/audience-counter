package com.cmp.audiencecounter.components

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.SaveButton
import org.junit.Rule
import org.junit.Test

class SaveButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSaveButtonClick() {
        var wasClicked = false

        composeTestRule.setContent {
            SaveButton(
                onClick = { wasClicked = true },
                isSaving = false,
                audience = 1
            )
        }

        composeTestRule.onNodeWithText("Salvar").performClick()
        assert(wasClicked)
    }
}