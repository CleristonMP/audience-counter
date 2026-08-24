package com.cmp.audiencecounter.components

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.components.RowCountDisplay
import org.junit.Rule
import org.junit.Test

class RowCountDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rowCountsAreAssociatedWithTheirOriginalRowNumbers() {
        val rowCounts = listOf(10, 20, 30)

        composeTestRule.setContent {
            RowCountDisplay(rowCounts)
        }

        rowCounts.forEachIndexed { index, count ->
            composeTestRule
                .onNodeWithText("Fileira ${index + 1}: $count pessoas")
                .assertExists()
        }
    }

    @Test
    fun singleRowUsesFirstRowNumber() {
        val rowCounts = listOf(10)

        composeTestRule.setContent {
            RowCountDisplay(rowCounts)
        }

        composeTestRule.onNodeWithText("Fileira 1: ${rowCounts.first()} pessoas").assertExists()
    }
}
