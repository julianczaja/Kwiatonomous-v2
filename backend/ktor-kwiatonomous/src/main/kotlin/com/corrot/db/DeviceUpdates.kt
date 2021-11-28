package com.corrot.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DeviceUpdates : Table() {
    val updateID = integer("updateID").autoIncrement().primaryKey()
    val timestamp = long("timestamp")
    val batteryLevel = integer("batteryLevel")
    val batteryVoltage = float("batteryVoltage")
    val temperature = float("temperature")
    val humidity = float("humidity")
    val nextWatering = long("nextWatering")
    val deviceID = varchar("deviceID", 12).references(Devices.deviceID, onDelete = ReferenceOption.CASCADE)
}