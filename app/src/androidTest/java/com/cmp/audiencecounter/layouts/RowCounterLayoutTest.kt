package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cmp.audiencecounter.ui.layouts.RowCounterLayout
import org.junit.Rule
import org.junit.Test

class RowCounterLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRowCounterLayoutDisplaysCorrectlyAfterStarting() {
        composeTestRule.setContent {
            RowCounterLayout(
                savedAudiences = emptyList(),
                onSaveTotal = {}
            )
        }

        // Verificar se o botão "Iniciar Nova Contagem" está presente inicialmente
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").assertExists()

        // Simular o clique no botão "Iniciar Nova Contagem"
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").performClick()

        // Verificar se o campo de input para "Quantidade de Fileiras" é exibido após clicar no botão
        composeTestRule.onNodeWithText("Quantidade de Fileiras").assertExists()
    }

    @Test
    fun testNextRowButtonAppearsAfterInput() {
        composeTestRule.setContent {
            RowCounterLayout(
                savedAudiences = emptyList(),
                onSaveTotal = {}
            )
        }

        // Iniciar a nova contagem
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").performClick()

        // Inserir valor no campo de "Quantidade de Fileiras"
        composeTestRule.onNodeWithText("Quantidade de Fileiras").performTextInput("3")

        // Verificar se o botão "Próxima Fileira" é exibido após o input
        composeTestRule.onNodeWithText("Próxima Fileira").assertExists()
    }

    @Test
    fun testSaveTotalButtonEnabledAfterAllRows() {
        composeTestRule.setContent {
            RowCounterLayout(
                savedAudiences = emptyList(),
                onSaveTotal = {}
            )
        }

        // Iniciar a nova contagem
        composeTestRule.onNodeWithText("Iniciar Nova Contagem").performClick()

        // Inserir valor no campo de "Quantidade de Fileiras"
        composeTestRule.onNodeWithText("Quantidade de Fileiras").performTextInput("3")

        // Simular o clique no botão "Próxima Fileira" para cada fileira
        composeTestRule.onNodeWithText("Próxima Fileira").performClick()
        composeTestRule.onNodeWithText("Próxima Fileira").performClick()
        composeTestRule.onNodeWithText("Finalizar Contagem").performClick()

        // Verificar se o botão "Salvar Total" é habilitado após contar todas as fileiras
        composeTestRule.onNodeWithText("Salvar Total").assertExists()
    }
}
