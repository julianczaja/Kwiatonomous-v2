package com.corrot.db.data.model

import com.corrot.db.data.dto.DeviceEventDto

data class DeviceEvent(
    val eventId: Int,
    val deviceId: String,
    val timestamp: Long,
    val type: String,
    val data: String
)

fun DeviceEvent.toDeviceEventDto() = DeviceEventDto(
    timestamp = timestamp,
    type = type,
    data = data
)