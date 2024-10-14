package com.cmp.audiencecounter.utils

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UtilsTest {

    @Test
    fun testGetCurrentFormattedDate() {
        val currentDate = getCurrentFormattedDateForTest()
        val expectedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())

        assertTrue(currentDate == expectedDate)
    }
}

fun getCurrentFormattedDateForTest(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date()) + " " + timeFormatter.format(Date())
}