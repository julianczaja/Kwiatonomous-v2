package com.corrot.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DevicesConfigurations : Table() {
    val sleepTimeMinutes = integer("sleepTimeMinutes")
    val wateringOn = bool("wateringOn")
    val wateringIntervalDays = integer("wateringIntervalDays")
    val wateringAmount = integer("wateringAmount")
    val wateringTime = varchar("wateringTime", 6) // MM:HH
    val deviceID = varchar("deviceID", 12).references(Devices.deviceID, onDelete = ReferenceOption.CASCADE)
}