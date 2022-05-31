package com.corrot.db.data.model

data class DeviceConfiguration(
    val deviceId: String,
    val sleepTimeMinutes: Int,
    val timeZoneOffset: Int,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: String
)