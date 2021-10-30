package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

data class DeviceUpdateDto(
    val updateID: Int,
    val deviceID: String,
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)

fun DeviceUpdateDto.toDeviceUpdate() = DeviceUpdate(
    deviceID = deviceID,
    updateTime = timestamp.toLocalDateTime(),
    batteryLevel = batteryLevel,
    batteryVoltage = batteryVoltage,
    temperature = temperature,
    humidity = humidity
)
