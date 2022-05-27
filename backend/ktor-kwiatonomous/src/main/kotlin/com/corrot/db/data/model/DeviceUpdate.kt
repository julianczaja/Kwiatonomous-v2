package com.corrot.db.data.model

data class DeviceUpdate(
    val updateId: Int,
    val deviceId: String,
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)
