package com.cmp.audiencecounter.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun getCurrentFormattedDate(): String {
    val formattedDateTime = with(LocalContext.current) {
        val formatter = DateFormat.getDateFormat(this)
        val timeFormatter = DateFormat.getTimeFormat(this)
        formatter.format(Date()) + " " + timeFormatter.format(Date())
    }
    return formattedDateTime
}
