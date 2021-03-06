package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import java.time.LocalDateTime

@Entity(tableName = "device_update")
data class DeviceUpdateEntity(
    val deviceId: String,
    @PrimaryKey val updateTime: LocalDateTime,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float
)

fun DeviceUpdateEntity.toDeviceUpdate() = DeviceUpdate(
    deviceId = deviceId,
    updateTime = updateTime,
    batteryLevel = batteryLevel,
    batteryVoltage = batteryVoltage,
    temperature = temperature,
    humidity = humidity
)
