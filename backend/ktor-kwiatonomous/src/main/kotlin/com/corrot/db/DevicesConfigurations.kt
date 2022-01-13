package com.corrot.db

import com.corrot.Constants.DEFAULT_SLEEP_TIME_MINUTES
import com.corrot.Constants.DEFAULT_TIME_ZONE_OFFSET
import com.corrot.Constants.DEFAULT_WATERING_AMOUNT
import com.corrot.Constants.DEFAULT_WATERING_INTERVAL_DAYS
import com.corrot.Constants.DEFAULT_WATERING_ON
import com.corrot.Constants.DEFAULT_WATERING_TIME
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DevicesConfigurations : Table() {
    val sleepTimeMinutes = integer("sleepTimeMinutes").default(DEFAULT_SLEEP_TIME_MINUTES)
    val timeZoneOffset = integer("timeZoneOffset").default(DEFAULT_TIME_ZONE_OFFSET)
    val wateringOn = bool("wateringOn").default(DEFAULT_WATERING_ON)
    val wateringIntervalDays = integer("wateringIntervalDays").default(DEFAULT_WATERING_INTERVAL_DAYS)
    val wateringAmount = integer("wateringAmount").default(DEFAULT_WATERING_AMOUNT)
    val wateringTime = varchar("wateringTime", 6).default(DEFAULT_WATERING_TIME)

    val deviceId = varchar("deviceId", 12).references(Devices.deviceId, onDelete = ReferenceOption.CASCADE)
}