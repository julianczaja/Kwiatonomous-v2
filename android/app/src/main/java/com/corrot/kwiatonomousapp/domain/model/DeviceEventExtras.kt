package com.corrot.kwiatonomousapp.domain.model

sealed class DeviceEventExtras {

    data class UserNote(
        val userName: String,
        val title: String,
        val content: String
    ) : DeviceEventExtras()

    data class LowBattery(
        val batteryLevel: Int,
        val batteryVoltage: Float
    ) : DeviceEventExtras()
}