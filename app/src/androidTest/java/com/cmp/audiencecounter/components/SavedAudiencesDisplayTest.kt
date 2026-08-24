package com.cmp.audiencecounter.components

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay
import com.cmp.audiencecounter.utils.formatAudienceTimestamp
import org.junit.Rule
import org.junit.Test

class SavedAudiencesDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSavedAudiencesDisplayShowsCorrectAudiences() {
        val audiences = listOf(
            AudienceRecord(1_720_611_000_000L, 25),
            AudienceRecord(1_720_626_300_000L, 30)
        )

        composeTestRule.setContent {
            SavedAudiencesDisplay(savedAudiences = audiences)
        }

        // Verifica se as contagens salvas estão sendo exibidas corretamente
        audiences.forEach { record ->
            val dateTime = formatAudienceTimestamp(record.timestampMillis)
            composeTestRule.onNodeWithText("$dateTime - ${record.count} pessoas").assertExists()
        }
    }

    @Test
    fun testSavedAudiencesDisplayEmpty() {
        val audiences = emptyList<AudienceRecord>()

        composeTestRule.setContent {
            SavedAudiencesDisplay(savedAudiences = audiences)
        }

        // Verifica se a mensagem de lista vazia é exibida
        composeTestRule.onNodeWithText("Nenhuma assistência salva").assertExists()
    }
}
