package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import java.time.LocalTime
import java.time.ZoneOffset

@Entity(tableName = "device_configuration")
data class DeviceConfigurationEntity(

    @PrimaryKey
    val deviceId: String,

    val sleepTimeMinutes: Int,
    val timeZoneOffset: ZoneOffset,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: LocalTime
)

fun DeviceConfigurationEntity.toDeviceConfiguration() = DeviceConfiguration(
    deviceId = deviceId,
    sleepTimeMinutes = sleepTimeMinutes,
    timeZoneOffset = timeZoneOffset,
    wateringOn = wateringOn,
    wateringIntervalDays = wateringIntervalDays,
    wateringAmount = wateringAmount,
    wateringTime = wateringTime,
)
