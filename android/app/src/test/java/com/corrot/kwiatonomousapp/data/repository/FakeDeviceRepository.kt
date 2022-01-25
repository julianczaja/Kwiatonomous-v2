package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDevice
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class FakeDeviceRepository : DeviceRepository {

    private val backendDevices = listOf(
        DeviceDto("id1", 1640174451L, 1640174451L, 1640174451L),
        DeviceDto("id2", 1640174452L, 1640174452L, 1640174452L),
        DeviceDto("id3", 1640174453L, 1640174453L, 1640174453L),
    )

    private val databaseDevices = mutableListOf<DeviceEntity>()

    override suspend fun fetchDevices(): List<DeviceDto> {
        return backendDevices
    }

    override suspend fun fetchDeviceById(id: String): DeviceDto {
        backendDevices.find { it.deviceId == id }?.let {
            return it
        }
        throw Exception("Can't find device")
    }

    override suspend fun fetchNextWateringByDeviceId(id: String): LocalDateTime {
        backendDevices.find { it.deviceId == id }?.let {
            return it.nextWatering.toLocalDateTime()
        }
        throw Exception("Can't find device")
    }

    override suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime) {
        backendDevices.find { it.deviceId == id }?.let {
            it.nextWatering = nextWatering.toLong()
        }
        throw Exception("Can't find device")
    }

    override fun getDeviceFromDatabase(deviceId: String) = flow {
        val found = databaseDevices.findLast { it.deviceId == deviceId }?.toDevice()
        found?.let {
            emit(it)
        }
    }

    override fun getDevicesFromDatabase(): Flow<List<Device>> = flow {
        emit(databaseDevices.map { it.toDevice() })
    }

    override suspend fun saveFetchedDevice(device: DeviceEntity) {
        databaseDevices.add(device)
    }

    override suspend fun saveFetchedDevices(devices: List<DeviceEntity>) {
        devices.forEach {
            databaseDevices.add(it)
        }
    }
}