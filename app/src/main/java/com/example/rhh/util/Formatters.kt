package com.example.rhh.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    fun ksh(v: Double): String {
        val rounded = v.toLong()
        return "KSh " + String.format(Locale.US, "%,d", rounded)
    }

    fun kshShort(v: Double): String {
        val abs = kotlin.math.abs(v)
        return when {
            abs >= 1_000_000 -> String.format(Locale.US, "%.1fM", v / 1_000_000)
            abs >= 1_000     -> String.format(Locale.US, "%.1fk", v / 1_000)
            else             -> v.toInt().toString()
        }
    }

    fun km(v: Double?): String =
        if (v == null) "—" else String.format(Locale.US, "%.1f km", v)

    fun dateShort(ms: Long): String =
        SimpleDateFormat("d MMM", Locale.US).format(Date(ms))

    fun dateTime(ms: Long): String =
        SimpleDateFormat("d MMM, HH:mm", Locale.US).format(Date(ms))
}
