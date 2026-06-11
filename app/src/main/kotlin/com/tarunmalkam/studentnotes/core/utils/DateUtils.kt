package com.tarunmalkam.studentnotes.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun now(): Long = System.currentTimeMillis()

    fun format(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}
