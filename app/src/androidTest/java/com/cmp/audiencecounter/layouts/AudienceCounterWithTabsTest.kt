package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.layouts.AudienceCounterWithTabs
import org.junit.Rule
import org.junit.Test

class AudienceCounterWithTabsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTabsAreDisplayedCorrectly() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = emptyList(),
                onSaveAudiences = {}
            )
        }

        // Verificar se as abas estão sendo exibidas corretamente
        composeTestRule.onNodeWithText("Contagem Direta").assertExists()
        composeTestRule.onNodeWithText("Contagem por Fileira").assertExists()
    }

    @Test
    fun testSwitchingTabs() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = emptyList(),
                onSaveAudiences = {}
            )
        }

        // Verificar o conteúdo da aba "Contagem Direta" inicialmente
        composeTestRule.onNodeWithText("Contagem Direta").assertExists()

        // Simular clique na aba "Contagem por Fileira"
        composeTestRule.onNodeWithText("Contagem por Fileira").performClick()

        // Verificar o conteúdo específico da aba "Contagem por Fileira"
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").assertExists()  // Conteúdo da aba de fileiras
    }
}
