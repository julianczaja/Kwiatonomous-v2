package com.corrot.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DeviceUpdates : Table() {
    val updateId = integer("updateId").autoIncrement()
    val timestamp = long("timestamp")
    val batteryLevel = integer("batteryLevel")
    val batteryVoltage = float("batteryVoltage")
    val temperature = float("temperature")
    val humidity = float("humidity")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(updateId)

    val deviceId = varchar("deviceId", 12).references(Devices.deviceId, onDelete = ReferenceOption.CASCADE)
}