package com.cmp.audiencecounter.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentFormattedDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date())
}
