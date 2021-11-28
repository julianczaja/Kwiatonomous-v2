package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceConfiguration

interface DeviceConfigurationDao {

    fun getDeviceConfiguration(deviceID: String): DeviceConfiguration?

    fun createDeviceConfiguration(
        deviceID: String,
        sleepTimeMinutes: Int,
        wateringOn: Boolean,
        wateringIntervalDays: Int,
        wateringAmount: Int
    )

    fun updateDeviceConfiguration(deviceID: String, deviceConfiguration: DeviceConfiguration)
}