package com.corrot.kwiatonomousapp.domain.model

data class UserDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceImageName: String,
    val isFavourite: Boolean = false,
    val notificationsOn: Boolean = false,
)
