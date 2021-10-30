package com.corrot.kwiatonomousapp.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val DEFAULT_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm:ss"

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), TimeZone.getDefault().toZoneId())

fun LocalDateTime.toFormattedString() =
    this.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT))