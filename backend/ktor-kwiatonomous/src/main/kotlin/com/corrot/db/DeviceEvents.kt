package com.corrot.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DeviceEvents : Table() {
    val eventId = integer("updateId").autoIncrement()
    val timestamp = long("timestamp")
    val type = varchar("type", length = 64)
    val data = varchar("data", length = 512)


    override val primaryKey: PrimaryKey
        get() = PrimaryKey(eventId)

    val deviceId = varchar("deviceId", 12).references(Devices.deviceId, onDelete = ReferenceOption.CASCADE)
}
