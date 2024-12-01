package com.corrot.db.data.dao

import com.corrot.db.data.dto.DeviceEventDto
import com.corrot.db.data.model.DeviceEvent

interface DeviceEventDao {

    fun getAllDeviceEvents(): List<DeviceEvent>

    fun getAllDeviceEvents(
        deviceId: String,
        limit: Int? = null,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null
    ): List<DeviceEvent>

    fun createDeviceEvent(
        deviceId: String,
        timestamp: Long,
        type: String,
        data: String
    )

    fun deleteDeviceEvent(eventId: Int)

    fun deleteDeviceEvent(deviceEventDto: DeviceEventDto)

    fun deleteDeviceEvent(timestamp: Long)
}