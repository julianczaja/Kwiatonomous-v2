package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEventEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceEventDto
import com.google.gson.Gson
import java.time.LocalDateTime

sealed class DeviceEvent(
    val deviceId: String,
    val timestamp: LocalDateTime,
    val extras: DeviceEventExtras? = null,
) {
    class Watering(
        deviceId: String,
        timestamp: LocalDateTime,
    ) : DeviceEvent(deviceId, timestamp)

    class ConfigurationChange(
        deviceId: String,
        timestamp: LocalDateTime,
    ) : DeviceEvent(deviceId, timestamp)

    class UserNote(
        userName: String,
        title: String,
        content: String,
        deviceId: String,
        timestamp: LocalDateTime,
    ) : DeviceEvent(deviceId, timestamp, DeviceEventExtras.UserNote(userName, title, content))

    class LowBattery(
        batteryLevel: Int,
        batteryVoltage: Float,
        deviceId: String,
        timestamp: LocalDateTime,
    ) : DeviceEvent(deviceId,
        timestamp,
        DeviceEventExtras.LowBattery(batteryLevel, batteryVoltage))

    class PumpCleaning(
        deviceId: String,
        timestamp: LocalDateTime,
    ) : DeviceEvent(deviceId, timestamp)

    fun toDeviceEventDto() = DeviceEventDto(
        timestamp = timestamp.toLong(),
        type = this.javaClass.simpleName,
        data = dataToString()
    )

    fun toDeviceEventEntity() = DeviceEventEntity(deviceId = deviceId,
        timestamp = timestamp,
        type = this.javaClass.simpleName,
        data = dataToString()
    )

    private fun dataToString() = Gson().toJson(this.extras)

    companion object {
        fun createFromTypeAndData(
            deviceId: String,
            timestamp: Long,
            type: String,
            data: String,
        ) = createFromTypeAndData(deviceId, timestamp.toLocalDateTime(), type, data)

        fun createFromTypeAndData(
            deviceId: String,
            timestamp: LocalDateTime,
            type: String,
            data: String,
        ) = when (type) {
            Watering::class.simpleName -> Watering(deviceId, timestamp)
            ConfigurationChange::class.simpleName -> ConfigurationChange(deviceId, timestamp)
            PumpCleaning::class.simpleName -> PumpCleaning(deviceId, timestamp)
            UserNote::class.simpleName -> {
                val extras = Gson().fromJson(data, DeviceEventExtras.UserNote::class.java)
                UserNote(deviceId = deviceId,
                    timestamp = timestamp,
                    userName = extras.userName,
                    title = extras.title,
                    content = extras.content)
            }
            LowBattery::class.simpleName -> {
                val extras = Gson().fromJson(data, DeviceEventExtras.LowBattery::class.java)
                LowBattery(deviceId = deviceId,
                    timestamp = timestamp,
                    batteryLevel = extras.batteryLevel,
                    batteryVoltage = extras.batteryVoltage)
            }
            else -> throw Exception("Unknown device event ('$type')")
        }
    }
}
