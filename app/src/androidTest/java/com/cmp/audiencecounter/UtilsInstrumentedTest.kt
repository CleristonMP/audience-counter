package com.cmp.audiencecounter

import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cmp.audiencecounter.utils.getCurrentFormattedDate
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGetCurrentFormattedDate() {
        composeTestRule.setContent {
            val currentDate = getCurrentFormattedDate()
            assertNotNull(currentDate)
        }
    }

    @Test
    fun testDateIsNotEmpty() {
        composeTestRule.setContent {
            val currentDate = getCurrentFormattedDate()
            assertTrue("A data não deve estar vazia", currentDate.isNotEmpty())
        }
    }

    @Test
    fun testDateIsToday() {
        composeTestRule.setContent {
            val currentDate = getCurrentFormattedDate()
            val expectedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            Log.d("Test", "currentDate: $currentDate")
            Log.d("Test", "expectedDate: $expectedDate")

            assertTrue("A data não corresponde à data de hoje", currentDate.startsWith(expectedDate))
        }
    }

    @Test
    fun testDateFormatIsCorrect() {
        composeTestRule.setContent {
            val currentDate = getCurrentFormattedDate()
            Log.d("Test", "currentDate: $currentDate")

            val regex = Regex("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
            Log.d("Test", "Regex match: ${regex.matches(currentDate)}")

            assertTrue("A data não está no formato correto", regex.matches(currentDate))
        }
    }
}
