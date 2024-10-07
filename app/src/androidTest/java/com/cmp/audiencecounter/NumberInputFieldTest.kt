package com.cmp.audiencecounter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.components.NumberInputField
import org.junit.Rule
import org.junit.Test

class NumberInputFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNumberInputField() {
        composeTestRule.setContent {
            NumberInputField(
                value = "5",
                onValueChange = {}
            )
        }

        // Verificar se o campo de texto exibe o valor correto
        composeTestRule.onNodeWithText("5").assertExists()
        composeTestRule.onNodeWithText("Quantidade de Fileiras").assertExists()
    }
}
