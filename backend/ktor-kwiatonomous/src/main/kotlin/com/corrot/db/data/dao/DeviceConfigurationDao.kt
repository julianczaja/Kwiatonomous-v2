package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceConfiguration

interface DeviceConfigurationDao {

    fun getDeviceConfiguration(deviceID: String): DeviceConfiguration?

    fun createDeviceConfiguration(
        deviceID: String,
        sleepTimeMinutes: Int,
        timeZoneOffset: Int,
        wateringOn: Boolean,
        wateringIntervalDays: Int,
        wateringAmount: Int,
        wateringTime: String
    )

    fun updateDeviceConfiguration(deviceConfiguration: DeviceConfiguration)
}