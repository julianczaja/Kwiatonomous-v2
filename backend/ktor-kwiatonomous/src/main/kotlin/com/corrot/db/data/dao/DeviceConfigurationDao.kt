package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceConfiguration

interface DeviceConfigurationDao {

    fun getDeviceConfiguration(deviceId: String): DeviceConfiguration?

    fun createDeviceConfiguration(
        deviceId: String,
        sleepTimeMinutes: Int,
        timeZoneOffset: Int,
        wateringOn: Boolean,
        wateringIntervalDays: Int,
        wateringAmount: Int,
        wateringTime: String
    )

    fun updateDeviceConfiguration(deviceConfiguration: DeviceConfiguration)
}