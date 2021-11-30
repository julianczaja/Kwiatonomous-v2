package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toBoolean
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration

data class DeviceConfigurationDto(
    val sleepTimeMinutes: Int,
    val wateringOn: Int,
    val wateringIntervalDays: Int,
    val wateringAmount: Int
)

fun DeviceConfigurationDto.toDeviceConfiguration() = DeviceConfiguration(
    sleepTimeMinutes = sleepTimeMinutes,
    wateringOn = wateringOn.toBoolean(),
    wateringIntervalDays = wateringIntervalDays,
    wateringAmount = wateringAmount
)
