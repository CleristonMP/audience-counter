package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.MainActivity
import org.junit.Rule
import org.junit.Test

class MainActivityStateRestorationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun directCountSurvivesActivityRecreation() {
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("1").assertExists()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("1").assertExists()
    }

    @Test
    fun directCountSurvivesTabChanges() {
        composeTestRule.onNodeWithText("+").performClick()

        composeTestRule.onNodeWithText("Contagem por Fileira").performClick()
        composeTestRule.onNodeWithText("Contagem Direta").performClick()

        composeTestRule.onNodeWithText("1").assertExists()
    }
}
