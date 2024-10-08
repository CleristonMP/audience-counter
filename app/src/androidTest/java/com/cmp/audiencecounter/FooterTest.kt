package com.cmp.audiencecounter

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.ui.components.Footer
import org.junit.Rule
import org.junit.Test

class FooterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFooterDisplaysCorrectText() {
        composeTestRule.setContent {
            Footer(fontSize = 12.sp)
        }

        // Verificar se o texto do rodapé está sendo exibido corretamente
        composeTestRule.onNodeWithText("Criado por Cleriston Pereira").assertExists()
    }
}
