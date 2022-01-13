package com.corrot.db

import org.jetbrains.exposed.sql.Table

object Devices : Table() {
    val deviceId = varchar("deviceId", 12).primaryKey()
    val birthday = long("birthday")
    var lastUpdate = long("lastUpdate")
    var nextWatering = long("nextWatering")
}