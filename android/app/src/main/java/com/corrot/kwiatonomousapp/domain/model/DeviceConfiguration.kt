package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.common.toDatabaseString
import com.corrot.kwiatonomousapp.common.toInt
import com.corrot.kwiatonomousapp.common.totalHours
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import java.time.LocalTime
import java.time.ZoneOffset

data class DeviceConfiguration(
    val deviceId: String,
    val sleepTimeMinutes: Int,
    val timeZoneOffset: ZoneOffset,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: LocalTime
)

fun DeviceConfiguration.toDeviceConfigurationDto() =
    DeviceConfigurationDto(
        deviceId = this.deviceId,
        sleepTimeMinutes = this.sleepTimeMinutes,
        timeZoneOffset = this.timeZoneOffset.totalHours(),
        wateringOn = this.wateringOn.toInt(),
        wateringIntervalDays = this.wateringIntervalDays,
        wateringAmount = this.wateringAmount,
        wateringTime = this.wateringTime.toDatabaseString()
    )

fun DeviceConfiguration.toDeviceConfigurationEntity() = DeviceConfigurationEntity(
    deviceId = deviceId,
    sleepTimeMinutes = sleepTimeMinutes,
    timeZoneOffset = timeZoneOffset,
    wateringOn = wateringOn,
    wateringIntervalDays = wateringIntervalDays,
    wateringAmount = wateringAmount,
    wateringTime = wateringTime
)