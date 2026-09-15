package de.ziven.android.ui.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun todayIso(): String = LocalDate.now().format(isoFormatter)

fun mondayIsoOf(date: LocalDate = LocalDate.now()): String {
    return date.with(DayOfWeek.MONDAY).format(isoFormatter)
}

fun isoDayOfWeek(iso: String): Int {
    val date = LocalDate.parse(iso, isoFormatter)
    return when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }
}

fun isoDayName(iso: String): String {
    val date = LocalDate.parse(iso, isoFormatter)
    return date.format(DateTimeFormatter.ofPattern("EEEE"))
}
