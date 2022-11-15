package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.model.Device
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface DeviceRepository {

    // Remote
    suspend fun fetchDevices(): List<DeviceDto>

    suspend fun fetchDeviceById(id: String): DeviceDto

    suspend fun fetchNextWateringByDeviceId(id: String): LocalDateTime

    suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime)

    suspend fun updateLastPumpCleaningByDeviceIdRemote(id: String, lastPumpCleaning: LocalDateTime)

    // Local
    fun getDeviceFromDatabase(deviceId: String): Flow<Device>

    fun getDevicesFromDatabase(): Flow<List<Device>>

    suspend fun saveFetchedDevice(device: DeviceEntity)

    suspend fun saveFetchedDevices(devices: List<DeviceEntity>)

    suspend fun removeAll()
}