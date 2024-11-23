package com.corrot.kwiatonomousapp.common

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.TimeZone

const val DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm"
const val DAY_SECONDS = 86400L
const val DAY_MINUTES = 1440L

// Always convert as UTC, because device send datetime with applied zone offset
fun Long.toLocalDateTime(): LocalDateTime =
    if (this > 1e10) {
        // seconds
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this), TimeZone.getTimeZone("UTC").toZoneId()
        )
    } else {
        // milliseconds
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(this), TimeZone.getTimeZone("UTC").toZoneId()
        )
    }

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT))

fun LocalDateTime.toLong(): Long =
    this.toEpochSecond(ZoneOffset.UTC)

@SuppressLint("DefaultLocale")
fun LocalTime.toDatabaseString(): String = String.format("%02d:%02d", this.hour, this.minute)

fun ZoneOffset.totalHours(): Int =
    this.totalSeconds / 3600

fun getAllUTCZones(): List<String> {
    val list = mutableListOf<String>()

    for (i in -12..14) {
        val format = when {
            i > 0 -> "UTC%+03d:00"
            i < 0 -> "UTC%03d:00"
            else -> "UTC"
        }
        list.add(String.format(format, i))
    }

    return list
}

fun getMinutesUntilLocalTime(
    fromTime: LocalTime = LocalTime.now(),
    toTime: LocalTime,
): Long {
    val until = fromTime.until(toTime, ChronoUnit.MINUTES)

    return when {
        until >= 0 -> until
        else -> until + DAY_MINUTES
    }
}