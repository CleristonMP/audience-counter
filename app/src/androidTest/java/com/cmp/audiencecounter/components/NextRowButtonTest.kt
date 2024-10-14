package com.cmp.audiencecounter.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.NextRowButton
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class NextRowButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNextRowButtonDisplaysCorrectText() {
        composeTestRule.setContent {
            NextRowButton(
                onClick = {},
                currentRow = 1,
                rowCount = 5
            )
        }

        // Verificar se o botão exibe o texto correto
        composeTestRule.onNodeWithText("Próxima Fileira").assertExists()
    }

    @Test
    fun testNextRowButtonClick() {
        var clicked = false
        var currentRow = 1

        composeTestRule.setContent {
            NextRowButton(
                onClick = {
                    clicked = true
                    currentRow++
                },
                currentRow = currentRow,
                rowCount = 3
            )
        }

        // Simular clique no botão e verificar se a ação foi executada
        composeTestRule.onNodeWithText("Próxima Fileira").performClick()
        assertTrue("O botão não incrementou a fileira", currentRow == 2)
        assert(clicked)
    }
}
