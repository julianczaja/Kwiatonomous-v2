package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.Device
import java.time.LocalDateTime

@Entity(tableName = "device")
data class DeviceEntity(

    @PrimaryKey
    val deviceId: String,

    val birthday: LocalDateTime,
    val lastUpdate: LocalDateTime,
    var nextWatering: LocalDateTime,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

fun DeviceEntity.toDevice() = Device(
    deviceId = deviceId,
    birthday = birthday,
    lastUpdate = lastUpdate,
    nextWatering = nextWatering
)
