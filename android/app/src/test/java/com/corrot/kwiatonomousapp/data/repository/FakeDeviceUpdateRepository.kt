package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceUpdate
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.flow

class FakeDeviceUpdateRepository : DeviceUpdateRepository {

    private val backendDeviceUpdates = mutableListOf(
        DeviceUpdateDto(10, "id1", 1000000000L, 100, 4.25f, 22.5f, 55.5f),
        DeviceUpdateDto(11, "id1", 1100000000L, 90, 4.05f, 23.5f, 54.5f),
        DeviceUpdateDto(12, "id1", 1200000000L, 80, 3.75f, 24.5f, 53.5f),
        DeviceUpdateDto(13, "id1", 1300000000L, 70, 3.65f, 25.5f, 52.5f),
        DeviceUpdateDto(14, "id1", 1400000000L, 60, 3.55f, 26.5f, 51.5f),
    )

    private val databaseDevicesUpdates = mutableListOf<DeviceUpdateEntity>()

    override suspend fun fetchAllDeviceUpdates(id: String): List<DeviceUpdateDto> {
        backendDeviceUpdates.filter { it.deviceId == id }.let {
            if (it.isNotEmpty()) {
                return it
            } else {
                throw Exception("Can't find device")
            }
        }
    }

    override suspend fun fetchAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto> {
        backendDeviceUpdates.filter { it.deviceId == id }.let {
            if (it.isNotEmpty()) {
                return it.take(limit)
            } else {
                throw Exception("Can't find device")
            }
        }
    }

    override suspend fun fetchDeviceUpdatesByDate(
        id: String,
        from: Long,
        to: Long
    ): List<DeviceUpdateDto> {
        backendDeviceUpdates.filter { it.deviceId == id && it.timestamp >= from && it.timestamp <= to }
            .let {
                if (it.isNotEmpty()) {
                    return it
                } else {
                    throw Exception()
                }
            }
    }

    override fun getAllDeviceUpdatesFromDatabase(deviceId: String) = flow {
        emit(databaseDevicesUpdates.map { it.toDeviceUpdate() })
    }

    override fun getAllDeviceUpdatesFromDatabase(deviceId: String, limit: Int) = flow {
        emit(databaseDevicesUpdates
            .take(limit)
            .map { it.toDeviceUpdate() })
    }

    override fun getDeviceUpdatesByDateFromDatabase(deviceId: String, from: Long, to: Long) = flow {
        emit(databaseDevicesUpdates
            .filter { it.updateTime.toLong() in from..to }
            .map { it.toDeviceUpdate() })
    }

    override suspend fun saveFetchedDeviceUpdates(
        deviceId: String,
        deviceUpdates: List<DeviceUpdateEntity>
    ) {
        deviceUpdates.forEach {
            databaseDevicesUpdates.add(it)
        }
    }
}