package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.v2.createComposeRule
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
                isPersisting = false,
                onAddAudience = { _, onSuccess -> onSuccess() },
                onClearAudiences = {}
            )
        }

        // Verifica se o contador começa zerado
        composeTestRule.onNodeWithText("0").assertExists()

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
                isPersisting = false,
                onAddAudience = { _, onSuccess ->
                    saved = true
                    onSuccess()
                },
                onClearAudiences = {}
            )
        }

        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("Salvar").performClick()

        // Verifica se a função de salvar foi chamada
        assert(saved)
    }
}
