package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.layouts.DirectCounterEvents
import com.cmp.audiencecounter.ui.layouts.DirectCounterLayoutState
import com.cmp.audiencecounter.ui.layouts.RowCounterEvents
import com.cmp.audiencecounter.ui.layouts.RowCounterLayoutState
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeDirectCounterLayout
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeRowCounterLayout
import org.junit.Rule
import org.junit.Test

class OrientationLayoutsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun landscapeDirectLayoutDisplaysSharedCounterSections() {
        composeTestRule.setContent {
            LandscapeDirectCounterLayout(
                state = DirectCounterLayoutState(
                    savedAudiences = emptyList(),
                    count = 1,
                    isSaving = false
                ),
                events = directEvents(),
                showClearConfirmation = false,
                onClearConfirmationChange = {}
            )
        }

        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("Salvar").assertExists()
        composeTestRule.onNodeWithText("+").assertExists()
    }

    @Test
    fun landscapeRowLayoutDisplaysSharedCompletedSummary() {
        composeTestRule.setContent {
            LandscapeRowCounterLayout(
                state = RowCounterLayoutState(
                    savedAudiences = emptyList(),
                    isSaving = false,
                    isCounting = false,
                    rowCount = 3,
                    currentRow = 4,
                    peopleInCurrentRow = 0,
                    completedRowCounts = listOf(10, 20, 30)
                ),
                events = rowEvents(),
                showClearConfirmation = false,
                onClearConfirmationChange = {}
            )
        }

        composeTestRule.onNodeWithText("Total de Pessoas: 60").assertExists()
        composeTestRule.onNodeWithText("Salvar Total").assertExists()
    }

    private fun directEvents() = DirectCounterEvents(
        onReset = {},
        onDecrement = {},
        onIncrement = {},
        onSave = {},
        onClearHistory = {}
    )

    private fun rowEvents() = RowCounterEvents(
        onSaveTotal = {},
        onClearHistory = {},
        onStart = {},
        onChangeRowCount = {},
        onCompleteCurrentRow = {},
        onResetCurrentRow = {},
        onDecrementCurrentRow = {},
        onIncrementCurrentRow = {}
    )
}
