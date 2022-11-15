package com.corrot.db.data.model

data class Device(
    val deviceId: String,
    val birthday: Long,
    var lastPumpCleaning: Long,
    var lastUpdate: Long,
    var nextWatering: Long
)
