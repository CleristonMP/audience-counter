package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.layouts.DirectCounterLayout
import org.junit.Rule
import org.junit.Test

class DirectCounterLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDirectCounterLayoutDisplaysCorrectly() {
        composeTestRule.setContent {
            DirectCounterLayout(
                savedAudiences = emptyList(),
                onSaveAudiences = {}
            )
        }

        // Verifica se o título da contagem direta é exibido
        composeTestRule.onNodeWithText("Contador de Assistência").assertExists()

        // Verifica se o botão "Salvar" é exibido
        composeTestRule.onNodeWithText("Salvar").assertExists()

        // Verifica se o botão "Zerar" é exibido
        composeTestRule.onNodeWithText("Zerar").assertExists()
    }

    @Test
    fun testSavingAudience() {
        var saved = false
        composeTestRule.setContent {
            DirectCounterLayout(
                savedAudiences = emptyList(),
                onSaveAudiences = { saved = true }
            )
        }

        // Simula o clique no botão "Salvar"
        composeTestRule.onNodeWithText("Salvar").performClick()

        // Verifica se a função de salvar foi chamada
        assert(saved)
    }
}