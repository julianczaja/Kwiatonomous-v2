package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import java.time.LocalDateTime

data class Device(
    val deviceId: String,
    val birthday: LocalDateTime,
    val lastUpdate: LocalDateTime,
    var nextWatering: LocalDateTime
)

fun Device.toDeviceEntity() = DeviceEntity(
    deviceId = deviceId,
    birthday = birthday,
    lastUpdate = lastUpdate,
    nextWatering = nextWatering
)