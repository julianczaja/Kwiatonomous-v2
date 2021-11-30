package com.corrot.kwiatonomousapp.domain.model

data class DeviceConfiguration(
    val sleepTimeMinutes: Int,
    val wateringOn: Boolean,
    val wateringIntervalDays: Int,
    val wateringAmount: Int
)