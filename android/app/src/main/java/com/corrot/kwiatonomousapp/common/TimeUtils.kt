package com.corrot.kwiatonomousapp.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

const val DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm"

// Always convert as UTC, because device send datetime with applied zone offset
fun Long.toLocalDateTime(): LocalDateTime =
    if (this > 1e10)
        // seconds
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this), TimeZone.getTimeZone("UTC").toZoneId()
        )
    else
        // milliseconds
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(this), TimeZone.getTimeZone("UTC").toZoneId()
        )

fun LocalDateTime.toFormattedString(): String =
    this.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT))

fun LocalDateTime.toLong(): Long =
    this.toEpochSecond(ZoneOffset.UTC)

fun LocalTime.toDatabaseString(): String =
    String.format("%02d:%02d", this.hour, this.minute)

fun ZoneOffset.totalHours(): Int =
    this.totalSeconds / 3600