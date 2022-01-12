package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import java.time.LocalDateTime

data class DeviceUpdate(
    val deviceId: String,
    val updateTime: LocalDateTime,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)

fun DeviceUpdate.toDeviceUpdateEntity() = DeviceUpdateEntity(
    deviceId = deviceId,
    updateTime = updateTime,
    batteryLevel = batteryLevel,
    batteryVoltage = batteryVoltage,
    temperature = temperature,
    humidity = humidity
)