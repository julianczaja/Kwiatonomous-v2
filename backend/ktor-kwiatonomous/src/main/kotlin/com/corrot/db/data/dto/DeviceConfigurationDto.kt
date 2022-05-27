package com.corrot.db.data.dto

data class DeviceConfigurationDto(
        val sleepTimeMinutes: Int,
        val timeZoneOffset: Int,
        val wateringOn: Int,
        val wateringIntervalDays: Int,
        val wateringAmount: Int,
        val wateringTime: String
) {
    override fun toString(): String {
        return "> Sleep time: $sleepTimeMinutes minutes\n" +
                "> Watering on: $wateringOn\n" +
                "> time zone offset: $timeZoneOffset\n" +
                "> Watering interval: $wateringIntervalDays days\n" +
                "> Watering amount: $wateringAmount ml\n" +
                "> Watering time: $wateringTime"
    }
}
