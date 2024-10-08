package com.cmp.audiencecounter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay
import org.junit.Rule
import org.junit.Test

class SavedAudiencesDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSavedAudiencesDisplayShowsCorrectAudiences() {
        val audiences: List<Pair<String, Int>> = listOf(
            "10/07/2024 12:30" to 25,
            "10/07/2024 16:45" to 30
        )

        composeTestRule.setContent {
            SavedAudiencesDisplay(savedAudiences = audiences)
        }

        // Verifica se as contagens salvas estão sendo exibidas corretamente
        audiences.forEach { (dateTime, count) ->
            composeTestRule.onNodeWithText("$dateTime - $count pessoas").assertExists()
        }
    }

    @Test
    fun testSavedAudiencesDisplayEmpty() {
        val audiences = emptyList<Pair<String, Int>>()

        composeTestRule.setContent {
            SavedAudiencesDisplay(savedAudiences = audiences)
        }

        // Verifica se a mensagem de lista vazia é exibida
        composeTestRule.onNodeWithText("Nenhuma assistência salva").assertExists()
    }
}
