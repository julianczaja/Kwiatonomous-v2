package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.domain.model.Device

// DTO to ease later use when project will be more complex
data class DeviceDto(
    val deviceID: String,
    val birthday: Long,
    val lastUpdate: Long,
)


fun DeviceDto.toDevice() = Device(
    id = this.deviceID,
    birthday = birthday.toLocalDateTime(),
    lastUpdate = lastUpdate.toLocalDateTime()
)