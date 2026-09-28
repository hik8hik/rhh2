package com.example.rhh.util

import java.util.Calendar

object TimeUtil {

    /** Fraction of the day elapsed, 0.0 .. 1.0 */
    fun fractionOfDay(cal: Calendar = Calendar.getInstance()): Float {
        val minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val seconds = minutes * 60 + cal.get(Calendar.SECOND)
        return seconds.toFloat() / 86400f
    }

    /** Seconds remaining until midnight. */
    fun secondsLeftToday(cal: Calendar = Calendar.getInstance()): Int {
        val used = cal.get(Calendar.HOUR_OF_DAY) * 3600 +
                   cal.get(Calendar.MINUTE) * 60 +
                   cal.get(Calendar.SECOND)
        return 86400 - used
    }

    fun hourOfDay(cal: Calendar = Calendar.getInstance()) = cal.get(Calendar.HOUR_OF_DAY)

    fun clockString(cal: Calendar = Calendar.getInstance()): String {
        val h = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val m = cal.get(Calendar.MINUTE).toString().padStart(2, '0')
        return "$h:$m"
    }

    /** Same calendar day check. */
    fun sameDay(a: Long, b: Long = System.currentTimeMillis()): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
               ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
