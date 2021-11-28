package com.corrot.db.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceUpdateDto(
    val timestamp: Long,
    val batteryLevel: Int,
    val batteryVoltage: Float,
    val temperature: Float,
    val humidity: Float,
    val nextWatering: Long
) {
    override fun toString(): String {
        return "> Timestamp: $timestamp\n" +
                "> Battery: $batteryLevel% ($batteryVoltage V)\n" +
                "> Temp: $temperature°C, Humidity: $humidity%\n" +
                "> Next watering: $nextWatering"
    }
}
