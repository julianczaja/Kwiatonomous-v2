package com.corrot.kwiatonomousapp.domain.model

import androidx.annotation.DrawableRes

data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    @DrawableRes val deviceImageId: Int,
    val isFavourite: Boolean = false,
    val notificationsOn: Boolean = false,
)
