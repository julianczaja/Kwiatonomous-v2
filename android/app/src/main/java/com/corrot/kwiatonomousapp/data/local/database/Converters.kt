package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.TypeConverter
import com.corrot.kwiatonomousapp.common.toDatabaseString
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.common.totalHours
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun localDateTimeToLong(localDateTime: LocalDateTime) = localDateTime.toLong()

    @TypeConverter
    fun longToLocalDateTime(long: Long) = long.toLocalDateTime()

    @TypeConverter
    fun zoneOffsetToInt(zoneOffset: ZoneOffset) = zoneOffset.totalHours()

    @TypeConverter
    fun intToZoneOffset(int: Int): ZoneOffset = ZoneOffset.ofHours(int)

    @TypeConverter
    fun localTimeToString(localTime: LocalTime) = localTime.toDatabaseString()

    @TypeConverter
    fun stringToLocalDateTime(string: String): LocalTime = LocalTime.parse(
        string, DateTimeFormatter.ofPattern("[H:mm][H:m][HH:m][HH:mm]")
    )
}