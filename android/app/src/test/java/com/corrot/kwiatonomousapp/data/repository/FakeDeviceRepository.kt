package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import java.time.LocalDateTime

class FakeDeviceRepository : DeviceRepository {

    private val devices = listOf(
        DeviceDto("id1", 1640174451L, 1640174451L, 1640174451L),
        DeviceDto("id2", 1640174452L, 1640174452L, 1640174452L),
        DeviceDto("id3", 1640174453L, 1640174453L, 1640174453L),
    )

    override suspend fun getDevices(): List<DeviceDto> {
        return devices
    }

    override suspend fun getDeviceById(id: String): DeviceDto {
        devices.find { it.deviceID == id }?.let {
            return it
        }
        throw Exception("Can't find device")
    }

    override suspend fun getNextWateringByDeviceId(id: String): LocalDateTime {
        devices.find { it.deviceID == id }?.let {
            return it.nextWatering.toLocalDateTime()
        }
        throw Exception("Can't find device")
    }

    override suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime) {
        devices.find { it.deviceID == id }?.let {
            it.nextWatering = nextWatering.toLong()
        }
        throw Exception("Can't find device")
    }
}