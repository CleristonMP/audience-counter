package com.cmp.audiencecounter.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.ConfirmationDialog
import org.junit.Rule
import org.junit.Test

class ConfirmationDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testConfirmationDialogDisplaysCorrectly() {
        composeTestRule.setContent {
            ConfirmationDialog(
                message = "Está certo sobre limpar as contagens?",
                onConfirm = {},
                onDismiss = {},
                showDialog = true,
                title = "Confirme a deleção"
            )
        }

        // Verificar se a mensagem de confirmação é exibida
        composeTestRule.onNodeWithText("Está certo sobre limpar as contagens?").assertExists()

        // Verificar se os botões de confirmação e cancelamento estão presentes
        composeTestRule.onNodeWithText("Limpar registros").assertExists()
        composeTestRule.onNodeWithText("Cancelar").assertExists()
    }

    @Test
    fun testConfirmButtonClick() {
        var confirmed = false
        composeTestRule.setContent {
            ConfirmationDialog(
                message = "Está certo sobre limpar as contagens?",
                onConfirm = { confirmed = true },
                onDismiss = {},
                showDialog = true,
                title = "Confirme a deleção"
            )
        }

        // Simular o clique no botão de confirmação
        composeTestRule.onNodeWithText("Limpar registros").performClick()
        assert(confirmed)
    }

    @Test
    fun testCancelButtonClick() {
        var cancelled = false
        composeTestRule.setContent {
            ConfirmationDialog(
                message = "Você tem certeza?",
                onConfirm = {},
                onDismiss = { cancelled = true },
                showDialog = true,
                title = "Confirme a deleção"
            )
        }

        // Simular o clique no botão de cancelamento
        composeTestRule.onNodeWithText("Cancelar").performClick()
        assert(cancelled)
    }
}
