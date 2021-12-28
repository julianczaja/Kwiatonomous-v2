package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository

class FakeDeviceUpdateRepository : DeviceUpdateRepository {

    private val deviceUpdates = mutableListOf(
        DeviceUpdateDto(10, "id1", 1000000000L, 100, 4.25f, 22.5f, 55.5f),
        DeviceUpdateDto(11, "id1", 1100000000L, 90, 4.05f, 23.5f, 54.5f),
        DeviceUpdateDto(12, "id1", 1200000000L, 80, 3.75f, 24.5f, 53.5f),
        DeviceUpdateDto(13, "id1", 1300000000L, 70, 3.65f, 25.5f, 52.5f),
        DeviceUpdateDto(14, "id1", 1400000000L, 60, 3.55f, 26.5f, 51.5f),
    )

    override suspend fun getAllDeviceUpdates(id: String): List<DeviceUpdateDto> {
        deviceUpdates.filter { it.deviceID == id }.let {
            if (it.isNotEmpty()) {
                return it
            } else {
                throw Exception("Can't find device")
            }
        }
    }

    override suspend fun getAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto> {
        deviceUpdates.filter { it.deviceID == id }.let {
            if (it.isNotEmpty()) {
                return it.take(limit)
            } else {
                throw Exception("Can't find device")
            }
        }
    }

    override suspend fun getDeviceUpdatesByDate(
        id: String,
        from: Long,
        to: Long
    ): List<DeviceUpdateDto> {
        deviceUpdates.filter { it.deviceID == id && it.timestamp >= from && it.timestamp <= to }
            .let {
                if (it.isNotEmpty()) {
                    return it
                } else {
                    throw Exception()
                }
            }
    }
}