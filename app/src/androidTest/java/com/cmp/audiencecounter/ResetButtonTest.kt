package com.cmp.audiencecounter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.ResetButton
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class ResetButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testResetButtonDisplaysCorrectText() {
        composeTestRule.setContent {
            ResetButton(
                onClick = {}
            )
        }

        // Verificar se o botão exibe o texto "Zerar"
        composeTestRule.onNodeWithText("Zerar").assertExists()
    }

    @Test
    fun testResetButtonClick() {
        var resetClicked = false
        var audience = 15

        composeTestRule.setContent {
            ResetButton(
                onClick = {
                    resetClicked = true
                    audience = 0
                }
            )
        }

        // Simular clique no botão e verificar se a ação foi executada
        composeTestRule.onNodeWithText("Zerar").performClick()
        assert(resetClicked)
        assertTrue("O botão não zerou a contagem", audience == 0)
    }
}
