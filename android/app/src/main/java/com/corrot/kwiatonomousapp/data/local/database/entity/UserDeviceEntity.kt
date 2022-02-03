package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.UserDevice

@Entity(tableName = "user_device")
data class UserDeviceEntity(
    @PrimaryKey
    val deviceId: String,
    val deviceName: String,
    val deviceImageId: Int,
    val isFavourite: Boolean = false
)

fun UserDeviceEntity.toUserDevice() = UserDevice(
    deviceId = deviceId,
    deviceName = deviceName,
    deviceImageId = deviceImageId,
    isFavourite = isFavourite,
)
