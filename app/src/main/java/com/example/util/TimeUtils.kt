package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun toRelativeTimeHindi(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < 0 -> "अभी-अभी"
            diff < minute -> "अभी-अभी"
            diff < hour -> "${(diff / minute).coerceAtLeast(1)} मिनट पहले"
            diff < day -> "${diff / hour} घंटे पहले"
            diff < 2 * day -> "कल"
            diff < 7 * day -> "${diff / day} दिन पहले"
            else -> {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale("hi", "IN"))
                sdf.format(Date(timestamp))
            }
        }
    }

    fun toFullDateHindi(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a", Locale("hi", "IN"))
        return sdf.format(Date(timestamp))
    }
}
