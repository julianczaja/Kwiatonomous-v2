package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEventEntity
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent

data class DeviceEventDto(
    val timestamp: Long,
    val type: String,
    val data: String,
)

fun DeviceEventDto.toDeviceEvent(deviceId: String) = DeviceEvent.createFromTypeAndData(
    deviceId, timestamp, type, data
)

fun DeviceEventDto.toDeviceEventEntity(deviceId: String) = DeviceEventEntity(
    deviceId, timestamp.toLocalDateTime(), type, data
)