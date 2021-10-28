package com.corrot.db

import org.jetbrains.exposed.sql.Table

object Devices : Table() {
    val deviceID = varchar("deviceID", 12).primaryKey()
    val birthday = long("birthday")
    var lastUpdate = long("lastUpdate")
}