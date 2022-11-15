package com.corrot.db.data.dao

import com.corrot.db.data.model.Device

interface DeviceDao {

    fun getAllDevices(): List<Device>

    fun getDevice(deviceId: String): Device?

    fun createDevice(deviceId: String, birthday: Long? = null)

    fun updateDevice(deviceId: String, lastUpdate: Long)

    fun updateNextWatering(deviceId: String, newNextWateringTime: Long)

    fun updateLastPumpCleaning(deviceId: String, newLastPumpCleaningTime: Long)

    fun deleteDevice(deviceId: String)
}