package com.corrot.db.data.dao

import com.corrot.db.data.model.DeviceUpdate

interface DeviceUpdateDao {

    fun getAllDeviceUpdates(): List<DeviceUpdate>

    fun getAllDeviceUpdates(
        deviceId: String,
        limit: Int? = null,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null
    ): List<DeviceUpdate>

    fun getDeviceUpdate(deviceId: String, updateId: Int): DeviceUpdate?

    fun createDeviceUpdate(
        deviceId: String,
        timestamp: Long,
        batteryLevel: Int,
        batteryVoltage: Float,
        temperature: Float,
        humidity: Float
    )

    fun deleteDeviceUpdate(updateId: Int)
}