package com.cmp.audiencecounter.layouts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.RowCounterLayout
import org.junit.Rule
import org.junit.Test

class RowCounterLayoutTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startActionDisplaysRowCountInput() {
        var uiState by mutableStateOf(AudienceCounterUiState())
        composeTestRule.setContent {
            RowCounterLayout(
                uiState = uiState,
                onAction = { action ->
                    if (action == AudienceCounterAction.StartRowCount) {
                        uiState = uiState.copy(isCountingRows = true)
                    }
                }
            )
        }

        composeTestRule.onNodeWithText("Iniciar Nova Contagem").performClick()

        composeTestRule.onNodeWithText("Quantidade de Fileiras").assertExists()
    }

    @Test
    fun rowCountInputEmitsChangeAndDisplaysNextRowButton() {
        var uiState by mutableStateOf(AudienceCounterUiState(isCountingRows = true))
        composeTestRule.setContent {
            RowCounterLayout(
                uiState = uiState,
                onAction = { action ->
                    if (action is AudienceCounterAction.ChangeRowCount) {
                        uiState = uiState.copy(rowCount = action.count)
                    }
                }
            )
        }

        composeTestRule.onNodeWithText("Quantidade de Fileiras").performTextInput("3")

        composeTestRule.onNodeWithText("Próxima Fileira").assertExists()
    }

    @Test
    fun saveTotalIsEnabledWhenAllRowsAreComplete() {
        composeTestRule.setContent {
            RowCounterLayout(
                uiState = AudienceCounterUiState(
                    rowCount = 3,
                    currentRow = 4,
                    completedRowCounts = listOf(1, 2, 3)
                ),
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("Salvar Total").assertIsEnabled()
    }
}
