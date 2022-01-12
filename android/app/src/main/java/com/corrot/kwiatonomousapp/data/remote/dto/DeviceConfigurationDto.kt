package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toBoolean
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class DeviceConfigurationDto(
    val deviceId: String,
    val sleepTimeMinutes: Int,
    val timeZoneOffset: Int,
    val wateringOn: Int,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: String
)

fun DeviceConfigurationDto.toDeviceConfiguration() =
    DeviceConfiguration(
        deviceId = deviceId,
        sleepTimeMinutes = sleepTimeMinutes,
        timeZoneOffset = ZoneOffset.ofHours(timeZoneOffset),
        wateringOn = wateringOn.toBoolean(),
        wateringIntervalDays = wateringIntervalDays,
        wateringAmount = wateringAmount,
        wateringTime = LocalTime.parse(
            wateringTime, DateTimeFormatter.ofPattern("[H:mm][H:m][HH:m][HH:mm]")
        )
    )

