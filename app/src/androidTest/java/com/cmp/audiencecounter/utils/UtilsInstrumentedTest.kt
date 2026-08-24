package com.cmp.audiencecounter.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    @Test
    fun testFormatAudienceTimestamp() {
        val currentDate = formatAudienceTimestamp(FIXED_TIMESTAMP)
        assertNotNull(currentDate)
    }

    @Test
    fun testDateIsNotEmpty() {
        val currentDate = formatAudienceTimestamp(FIXED_TIMESTAMP)
        assertTrue("A data não deve estar vazia", currentDate.isNotEmpty())
    }

    @Test
    fun testDateRepresentsProvidedTimestamp() {
        val currentDate = formatAudienceTimestamp(FIXED_TIMESTAMP)
        val expectedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(FIXED_TIMESTAMP)

        assertTrue("A data não corresponde ao timestamp", currentDate == expectedDate)
    }

    @Test
    fun testDateFormatIsCorrect() {
        val currentDate = formatAudienceTimestamp(FIXED_TIMESTAMP)
        val regex = Regex("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")

        assertTrue("A data não está no formato correto", regex.matches(currentDate))
    }

    private companion object {
        const val FIXED_TIMESTAMP = 1_777_000_000_000L
    }
}
