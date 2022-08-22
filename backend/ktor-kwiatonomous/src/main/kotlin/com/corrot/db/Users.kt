package com.corrot.db

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId = varchar("userId", 32).primaryKey()
    val userName = varchar("userName", 32)
    val ha1 = binary("ha1", 16)
    val devices = varchar("devices", 1024)
    var registrationTimestamp = long("registrationTimestamp")
    var lastActivityTimestamp = long("lastActivityTimestamp")
}