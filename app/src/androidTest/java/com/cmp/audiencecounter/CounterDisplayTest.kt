package com.cmp.audiencecounter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.components.CounterDisplay
import org.junit.Rule
import org.junit.Test

class CounterDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCounterDisplayShowsCorrectCount() {
        val currentCount = 5

        composeTestRule.setContent {
            CounterDisplay(audience = currentCount)
        }

        // Verificar se o valor da contagem está sendo exibido corretamente
        composeTestRule.onNodeWithText(currentCount.toString()).assertExists()
    }

    @Test
    fun testCounterDisplayZeroCount() {
        composeTestRule.setContent {
            CounterDisplay(audience = 0)
        }

        // Verificar se a contagem "0" está sendo exibida corretamente
        composeTestRule.onNodeWithText("0").assertExists()
    }
}
