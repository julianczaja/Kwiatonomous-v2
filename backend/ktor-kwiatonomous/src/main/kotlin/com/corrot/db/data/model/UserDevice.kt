package com.corrot.db.data.model

data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceImageName: String,
    val isFavourite: Boolean,
    val notificationsOn: Boolean,
)
