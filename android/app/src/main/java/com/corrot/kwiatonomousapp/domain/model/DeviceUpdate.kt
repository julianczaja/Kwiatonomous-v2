package com.corrot.kwiatonomousapp.domain.model

import java.time.LocalDateTime

data class DeviceUpdate(
    val deviceID: String,
    val updateTime: LocalDateTime,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)