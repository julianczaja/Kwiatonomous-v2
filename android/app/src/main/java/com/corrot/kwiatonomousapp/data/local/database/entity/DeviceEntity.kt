package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.common.Constants.LOCAL_DATE_TIME_MIN_STRING
import com.corrot.kwiatonomousapp.domain.model.Device
import java.time.LocalDateTime

@Entity(tableName = "device")
data class DeviceEntity(

    @PrimaryKey
    val deviceId: String,

    val birthday: LocalDateTime,
    @ColumnInfo(defaultValue = LOCAL_DATE_TIME_MIN_STRING)
    val lastPumpCleaning: LocalDateTime = LocalDateTime.MIN,
    val lastUpdate: LocalDateTime,
    var nextWatering: LocalDateTime
)

fun DeviceEntity.toDevice() = Device(
    deviceId = deviceId,
    birthday = birthday,
    lastPumpCleaning = lastPumpCleaning,
    lastUpdate = lastUpdate,
    nextWatering = nextWatering
)
