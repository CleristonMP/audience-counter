package com.cmp.audiencecounter.components

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cmp.audiencecounter.ui.components.ClearButton
import org.junit.Rule
import org.junit.Test

class ClearButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testClearButtonDisabled() {
        composeTestRule.setContent {
            ClearButton(isEnabled = false, onClick = {})
        }

        // Verificar se o botão aparece desabilitado
        composeTestRule.onNodeWithText("Limpar contagens")
            .assertIsNotEnabled()
    }

    @Test
    fun testClearButtonEnabled() {
        var wasClicked = false

        composeTestRule.setContent {
            ClearButton(isEnabled = true, onClick = { wasClicked = true })
        }

        // Verificar se o botão aparece habilitado e pode ser clicado
        composeTestRule.onNodeWithText("Limpar contagens").performClick()
        assert(wasClicked)
    }
}
