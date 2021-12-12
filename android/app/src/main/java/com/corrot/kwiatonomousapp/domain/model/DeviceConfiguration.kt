package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.common.toDatabaseString
import com.corrot.kwiatonomousapp.common.toInt
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import java.time.LocalTime

data class DeviceConfiguration(
    val sleepTimeMinutes: Int,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: LocalTime
)


fun DeviceConfiguration.toDeviceConfigurationDto() =
    DeviceConfigurationDto(
        sleepTimeMinutes = this.sleepTimeMinutes,
        wateringOn = this.wateringOn.toInt(),
        wateringIntervalDays = this.wateringIntervalDays,
        wateringAmount = this.wateringAmount,
        wateringTime = this.wateringTime.toDatabaseString()
    )