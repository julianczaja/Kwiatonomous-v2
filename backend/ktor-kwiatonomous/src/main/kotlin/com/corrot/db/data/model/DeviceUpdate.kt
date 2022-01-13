package com.corrot.db.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceUpdate(
    val updateId: Int,
    val deviceId: String,
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)
