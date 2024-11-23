package com.corrot.db

import org.jetbrains.exposed.sql.Table

object Devices : Table() {
    val deviceId = varchar("deviceId", 12)
    val birthday = long("birthday")
    var lastPumpCleaning = long("lastPumpCleaning").default(0L)
    var lastUpdate = long("lastUpdate")
    var nextWatering = long("nextWatering")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(deviceId)
}