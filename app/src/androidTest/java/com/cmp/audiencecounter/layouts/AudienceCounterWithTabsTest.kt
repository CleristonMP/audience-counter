package com.cmp.audiencecounter.layouts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterError
import com.cmp.audiencecounter.presentation.AudienceCounterTab
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.AudienceCounterWithTabs
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AudienceCounterWithTabsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tabsAreDisplayedCorrectly() {
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                uiState = AudienceCounterUiState(),
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("Contagem Direta").assertExists()
        composeTestRule.onNodeWithText("Contagem por Fileira").assertExists()
    }

    @Test
    fun selectingRowTabEmitsActionAndDisplaysRowContent() {
        var uiState by mutableStateOf(AudienceCounterUiState())
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                uiState = uiState,
                onAction = { action ->
                    if (action is AudienceCounterAction.SelectTab) {
                        uiState = uiState.copy(selectedTab = action.tab)
                    }
                }
            )
        }

        composeTestRule.onNodeWithText("Contagem por Fileira").performClick()

        composeTestRule.onNodeWithText("Iniciar Nova Contagem").assertExists()
    }

    @Test
    fun persistenceErrorIsDisplayedAndDismissed() {
        var uiState by mutableStateOf(
            AudienceCounterUiState(error = AudienceCounterError.PERSISTENCE)
        )
        composeTestRule.setContent {
            AudienceCounterWithTabs(
                uiState = uiState,
                onAction = { action ->
                    if (action == AudienceCounterAction.DismissError) {
                        uiState = uiState.copy(error = null)
                    }
                }
            )
        }

        composeTestRule
            .onNodeWithText("Não foi possível concluir a operação. Tente novamente.")
            .assertExists()
        composeTestRule.mainClock.advanceTimeBy(5_000)

        assertEquals(null, uiState.error)
    }
}
