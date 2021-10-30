package com.corrot.db.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceUpdateDto(
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)
