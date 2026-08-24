package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.layouts.AudienceCounterWithTabs
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AudienceCounterWithTabsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTabsAreDisplayedCorrectly() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = emptyList(),
                onAddAudience = {},
                onClearAudiences = {}
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
                onAddAudience = {},
                onClearAudiences = {}
            )
        }

        // Verificar o conteúdo da aba "Contagem Direta" inicialmente
        composeTestRule.onNodeWithText("Contagem Direta").assertExists()

        // Simular clique na aba "Contagem por Fileira"
        composeTestRule.onNodeWithText("Contagem por Fileira").performClick()

        // Verificar o conteúdo específico da aba "Contagem por Fileira"
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").assertExists()  // Conteúdo da aba de fileiras
    }

    @Test
    fun successfulSaveResetsCounterOnlyAfterPersistenceCompletes() {
        var savedCount: Int? = null
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = emptyList(),
                onAddAudience = { count -> savedCount = count },
                onClearAudiences = {}
            )
        }

        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("Salvar").performClick()
        composeTestRule.waitForIdle()

        assert(savedCount == 1)
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun failedSavePreservesCounterAndAllowsRetry() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = emptyList(),
                onAddAudience = { throw IOException("Write failed") },
                onClearAudiences = {}
            )
        }

        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("Salvar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("Salvar").assertIsEnabled()
        composeTestRule
            .onNodeWithText("Não foi possível concluir a operação. Tente novamente.")
            .assertExists()
    }

    @Test
    fun failedClearKeepsHistoryAndShowsError() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                savedAudiences = listOf("24/08/2026 10:15" to 25),
                onAddAudience = {},
                onClearAudiences = { throw IOException("Write failed") }
            )
        }

        composeTestRule.onNodeWithText("Limpar contagens").performClick()
        composeTestRule.onNodeWithText("Limpar registros").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("24/08/2026 10:15 - 25 pessoas").assertExists()
        composeTestRule
            .onNodeWithText("Não foi possível concluir a operação. Tente novamente.")
            .assertExists()
    }
}
