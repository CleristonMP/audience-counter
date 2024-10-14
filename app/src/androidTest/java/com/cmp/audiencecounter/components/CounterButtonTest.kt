package com.cmp.audiencecounter.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.ui.components.CounterButton
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class CounterButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testIncrementButtonDisplaysCorrectly() {
        composeTestRule.setContent {
            CounterButton(
                text = "+",
                onClick = {},
                backgroundColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                size = 120.dp,
                fontSize = 48.sp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )
        }

        // Verificar se o botão exibe o texto correto
        composeTestRule.onNodeWithText("+").assertExists()
    }

    @Test
    fun testIncrementButtonClick() {
        var clicked = false
        var audience = 0

        composeTestRule.setContent {
            CounterButton(
                text = "+",
                onClick = {
                    clicked = true
                    audience++
                },
                backgroundColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                size = 120.dp,
                fontSize = 48.sp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )
        }

        // Simular clique no botão e verificar se a ação foi executada
        composeTestRule.onNodeWithText("+").performClick()
        assertTrue("O botão não adicionou uma unidade", audience == 1)
        assert(clicked)
    }

    @Test
    fun testDecrementButtonDisplaysCorrectly() {
        composeTestRule.setContent {
            CounterButton(
                text = "-",
                onClick = {},
                backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                size = 60.dp,
                fontSize = 24.sp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )
        }

        // Verificar se o botão exibe o texto correto
        composeTestRule.onNodeWithText("-").assertExists()
    }

    @Test
    fun testDecrementButtonClick() {
        var clicked = false
        var audience = 1

        composeTestRule.setContent {
            CounterButton(
                text = "-",
                onClick = {
                    clicked = true
                    audience--
                },
                backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                size = 60.dp,
                fontSize = 24.sp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )
        }

        // Simular clique no botão e verificar se a ação foi executada
        composeTestRule.onNodeWithText("-").performClick()
        assertTrue("O botão não decrementou uma unidade", audience == 0)
        assert(clicked)
    }
}
