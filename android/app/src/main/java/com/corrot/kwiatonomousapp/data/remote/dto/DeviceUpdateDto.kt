package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

data class DeviceUpdateDto(
    val updateId: Int,
    val deviceId: String,
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)

fun DeviceUpdateDto.toDeviceUpdate() = DeviceUpdate(
    deviceId = deviceId,
    updateTime = timestamp.toLocalDateTime(),
    batteryLevel = batteryLevel,
    batteryVoltage = batteryVoltage,
    temperature = temperature,
    humidity = humidity
)
