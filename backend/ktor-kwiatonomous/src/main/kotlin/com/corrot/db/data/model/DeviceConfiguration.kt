package com.corrot.db.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceConfiguration(
    val deviceID: String,
    val sleepTimeMinutes: Int,
    val timeZoneOffset: Int,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int,
    val wateringTime: String
)