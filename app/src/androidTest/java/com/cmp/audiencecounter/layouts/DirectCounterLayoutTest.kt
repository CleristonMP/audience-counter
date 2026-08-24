package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.DirectCounterLayout
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DirectCounterLayoutTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun directCounterLayoutDisplaysCurrentState() {
        composeTestRule.setContent {
            DirectCounterLayout(
                uiState = AudienceCounterUiState(directCount = 5),
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("5").assertExists()
        composeTestRule.onNodeWithText("Salvar").assertExists()
        composeTestRule.onNodeWithText("Zerar").assertExists()
    }

    @Test
    fun saveButtonEmitsSaveAction() {
        var receivedAction: AudienceCounterAction? = null
        composeTestRule.setContent {
            DirectCounterLayout(
                uiState = AudienceCounterUiState(directCount = 1),
                onAction = { receivedAction = it }
            )
        }

        composeTestRule.onNodeWithText("Salvar").performClick()

        assertEquals(AudienceCounterAction.SaveDirectCount, receivedAction)
    }
}
