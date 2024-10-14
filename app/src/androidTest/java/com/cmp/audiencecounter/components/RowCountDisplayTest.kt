package com.cmp.audiencecounter.components

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.components.RowCountDisplay
import org.junit.Rule
import org.junit.Test

class RowCountDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRowCountDisplayShowsCorrectRow() {
        val currentRow = 2
        val peopleInRow = 5

        val rowCounts: SnapshotStateList<Int> = mutableListOf(10, 5, 13).toMutableStateList()

        composeTestRule.setContent {
            RowCountDisplay(rowCounts)
        }

        // Verificar se o display exibe a contagem correta das fileiras
        composeTestRule.onNodeWithText("Fileira $currentRow: $peopleInRow pessoas").assertExists()
    }

    @Test
    fun testRowCountDisplayWithOneRow() {
        val rowCounts: SnapshotStateList<Int> = mutableListOf(10).toMutableStateList()

        composeTestRule.setContent {
            RowCountDisplay(rowCounts)
        }

        // Verificar se a contagem de uma única fileira é exibida corretamente
        composeTestRule.onNodeWithText("Fileira 1: ${rowCounts.first()} pessoas").assertExists()
    }
}
