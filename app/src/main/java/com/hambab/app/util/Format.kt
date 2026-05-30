package com.hambab.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Web lib/format.ts 와 1:1.

private val TIME_FMT = SimpleDateFormat("M/d a h시", Locale.KOREAN)
private val DT_FMT = SimpleDateFormat("M/d HH:mm", Locale.KOREAN)

fun formatMeetTime(epochMs: Long): String {
    val d = Date(epochMs)
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply { time = d }

    val sameDay = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val isTomorrow = tomorrow.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
        tomorrow.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

    val h = target.get(Calendar.HOUR_OF_DAY)
    val m = target.get(Calendar.MINUTE)
    val ampm = if (h < 12) "오전" else "오후"
    val h12 = ((h % 12).takeIf { it != 0 } ?: 12)
    val mm = if (m == 0) "" else ":${m.toString().padStart(2, '0')}"
    val timeLabel = "$ampm $h12$mm 시"
    return when {
        sameDay -> "오늘 $timeLabel"
        isTomorrow -> "내일 $timeLabel"
        else -> "${target.get(Calendar.MONTH) + 1}/${target.get(Calendar.DAY_OF_MONTH)} $timeLabel"
    }
}

fun minutesUntil(epochMs: Long): Long = (epochMs - System.currentTimeMillis()) / 60_000L

fun formatCountdown(epochMs: Long): String {
    val m = minutesUntil(epochMs)
    return when {
        m < 0 -> "시작됨"
        m < 60 -> "${m}분 후"
        m < 24 * 60 -> "${m / 60}시간 후"
        else -> "${m / (24 * 60)}일 후"
    }
}

fun isInstantWindow(epochMs: Long): Boolean {
    val m = minutesUntil(epochMs)
    return m in -15..180
}

fun formatDateTime(epochMs: Long): String = DT_FMT.format(Date(epochMs))

@Suppress("unused")
fun formatTimeOnly(epochMs: Long): String = TIME_FMT.format(Date(epochMs))
