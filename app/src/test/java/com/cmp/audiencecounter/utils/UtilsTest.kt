package com.cmp.audiencecounter.utils

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UtilsTest {

    @Test
    fun getCurrentFormattedDateUsesExpectedFormat() {
        val formattedDate = getCurrentFormattedDate()

        assertTrue(formattedDate.matches(Regex("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")))
    }

    @Test
    fun getCurrentFormattedDateRepresentsCurrentMinute() {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
            isLenient = false
        }
        val beforeCall = System.currentTimeMillis()
        val formattedDate = getCurrentFormattedDate()
        val afterCall = System.currentTimeMillis()
        val parsedDate = requireNotNull(formatter.parse(formattedDate))
        val parsedMinute = TimeUnit.MILLISECONDS.toMinutes(parsedDate.time)

        val firstPossibleMinute = TimeUnit.MILLISECONDS.toMinutes(beforeCall)
        val lastPossibleMinute = TimeUnit.MILLISECONDS.toMinutes(afterCall)

        assertTrue(parsedMinute in firstPossibleMinute..lastPossibleMinute)
    }
}
