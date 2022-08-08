package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import java.time.LocalDateTime

@Entity(tableName = "device_event")
data class DeviceEventEntity(
    val deviceId: String,
    @PrimaryKey val timestamp: LocalDateTime,
    val type: String,
    val data: String,
)

fun DeviceEventEntity.toDeviceEvent() = DeviceEvent.createFromTypeAndData(
    deviceId, timestamp, type, data
)