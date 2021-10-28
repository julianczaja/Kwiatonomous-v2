package com.corrot.db.data.dao

import com.corrot.db.data.model.Device

interface DeviceDao {

    fun init()

    fun getAllDevices(): List<Device>

    fun getDevice(deviceID: String): Device?

    fun createDevice(deviceID: String, birthday: Long? = null)

    fun updateDevice(deviceID: String, lastUpdate: Long)

    fun deleteDevice(deviceID: String)
}