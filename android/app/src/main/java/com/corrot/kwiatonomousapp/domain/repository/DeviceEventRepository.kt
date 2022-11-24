package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEventEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceEventDto
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import kotlinx.coroutines.flow.Flow

interface DeviceEventRepository {

    // Remote
    suspend fun fetchAllDeviceEvents(deviceId: String): List<DeviceEventDto>

    suspend fun fetchAllDeviceEvents(deviceId: String, limit: Int): List<DeviceEventDto>

    suspend fun updateAllDeviceEvents(deviceId: String, limit: Int)

    suspend fun fetchDeviceEventsByDate(
        deviceId: String,
        from: Long,
        to: Long,
    ): List<DeviceEventDto>

    suspend fun addNewDeviceEventToBackend(deviceId: String, deviceEventDto: DeviceEventDto)

    suspend fun removeDeviceEventFromBackend(deviceId: String, deviceEventDto: DeviceEventDto)

    // Local
    fun getAllDeviceEventsFromDatabase(deviceId: String): Flow<List<DeviceEvent>>

    fun getAllDeviceEventsFromDatabase(deviceId: String, limit: Int): Flow<List<DeviceEvent>>

    fun getDeviceEventsByDateFromDatabase(
        deviceId: String,
        from: Long,
        to: Long,
    ): Flow<List<DeviceEvent>>

    suspend fun addNewDeviceEventToDatabase(deviceEventEntity: DeviceEventEntity)

    suspend fun removeDeviceEventFromDatabase(deviceId: String, timestamp: Long)

    suspend fun saveFetchedDeviceEvents(deviceEvents: List<DeviceEventEntity>)

    suspend fun removeAll()
}