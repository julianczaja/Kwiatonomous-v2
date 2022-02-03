package com.corrot.kwiatonomousapp.domain.model

import androidx.annotation.DrawableRes
import com.corrot.kwiatonomousapp.data.local.database.entity.UserDeviceEntity

data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    @DrawableRes
    val deviceImageId: Int,
    val isFavourite: Boolean = false
)

fun UserDevice.toUserDeviceEntity() = UserDeviceEntity(
    deviceId = deviceId,
    deviceName = deviceName,
    deviceImageId = deviceImageId,
    isFavourite = isFavourite,
)