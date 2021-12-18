package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceUpdate

interface DeviceUpdateDao {

    fun getAllDeviceUpdates(): List<DeviceUpdate>

    fun getAllDeviceUpdates(
        deviceID: String,
        limit: Int? = null,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null
    ): List<DeviceUpdate>

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