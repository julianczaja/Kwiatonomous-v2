package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import kotlinx.coroutines.flow.Flow

interface DeviceUpdateRepository {

    // Remote
    suspend fun fetchAllDeviceUpdates(id: String): List<DeviceUpdateDto>

    suspend fun fetchAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto>

    suspend fun fetchDeviceUpdatesByDate(id: String, from: Long, to: Long): List<DeviceUpdateDto>

    // Local
    fun getAllDeviceUpdatesFromDatabase(deviceId: String): Flow<List<DeviceUpdate>>

    fun getAllDeviceUpdatesFromDatabase(deviceId: String, limit: Int): Flow<List<DeviceUpdate>>

    fun getDeviceUpdatesByDateFromDatabase(deviceId: String, from: Long, to: Long): Flow<List<DeviceUpdate>>

    suspend fun saveFetchedDeviceUpdates(deviceId: String, deviceUpdates: List<DeviceUpdateEntity>)

}