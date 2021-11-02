package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceUpdate

interface DeviceUpdateDao {

    fun getAllDeviceUpdates(): List<DeviceUpdate>

    fun getAllDeviceUpdates(deviceID: String): List<DeviceUpdate>

    fun getLastDeviceUpdates(deviceID: String, count: Int): List<DeviceUpdate>

    fun getDeviceUpdate(deviceID: String, updateID: Int): DeviceUpdate?

    fun createDeviceUpdate(
        deviceID: String,
        timestamp: Long,
        batteryLevel: Int,
        batteryVoltage: Float,
        temperature: Float,
        humidity: Float
    ): Int

    fun deleteDeviceUpdate(updateID: Int)
}