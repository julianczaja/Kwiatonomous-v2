package com.corrot.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DevicesConfigurations : Table() {
    val sleepTimeMinutes = integer("sleepTimeMinutes")
    val wateringOn = bool("wateringOn").default(false)
    val timeZoneOffset = integer("timeZoneOffset").default(1)
    val wateringIntervalDays = integer("wateringIntervalDays").default(1)
    val wateringAmount = integer("wateringAmount").default(50)
    val wateringTime = varchar("wateringTime", 6).default("12:00") // MM:HH
    val deviceID = varchar("deviceID", 12).references(Devices.deviceID, onDelete = ReferenceOption.CASCADE)
}