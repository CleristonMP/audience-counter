package com.cmp.audiencecounter.utils

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UtilsTest {

    @Test
    fun formatAudienceTimestampUsesExpectedFormat() {
        val formattedDate = formatAudienceTimestamp(1_777_000_000_000L)

        assertTrue(formattedDate.matches(Regex("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")))
    }

    @Test
    fun formatAudienceTimestampRepresentsProvidedInstant() {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
            isLenient = false
        }
        val timestamp = 1_777_000_000_000L
        val formattedDate = formatAudienceTimestamp(timestamp)
        val parsedDate = requireNotNull(formatter.parse(formattedDate))

        assertTrue(kotlin.math.abs(parsedDate.time - timestamp) < 60_000)
    }
}
