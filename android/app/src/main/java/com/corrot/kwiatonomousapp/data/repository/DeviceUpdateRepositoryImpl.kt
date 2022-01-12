package com.corrot.kwiatonomousapp.data.repository

import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceUpdate
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceUpdateRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase
) : DeviceUpdateRepository {

    override suspend fun fetchAllDeviceUpdates(id: String): List<DeviceUpdateDto> {
        return kwiatonomousApi.getAllDeviceUpdates(id)
    }

    override suspend fun fetchAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto> {
        return kwiatonomousApi.getAllDeviceUpdates(id, limit)
    }

    override suspend fun fetchDeviceUpdatesByDate(
        id: String,
        from: Long,
        to: Long
    ): List<DeviceUpdateDto> {
        return kwiatonomousApi.getDeviceUpdatesByDate(id, from, to)
    }

    override fun getAllDeviceUpdatesFromDatabase(deviceId: String): Flow<List<DeviceUpdate>> {
        return kwiatonomousDb.deviceUpdateDao().getAllDeviceUpdates(deviceId).map { deviceUpdates ->
            deviceUpdates.map { it.toDeviceUpdate() }
        }
    }

    override fun getAllDeviceUpdatesFromDatabase(
        deviceId: String,
        limit: Int
    ): Flow<List<DeviceUpdate>> {
        return kwiatonomousDb.deviceUpdateDao().getAllDeviceUpdates(deviceId).map { deviceUpdates ->
            deviceUpdates.map { it.toDeviceUpdate() }
        }
    }

    override fun getDeviceUpdatesByDateFromDatabase(
        deviceId: String,
        from: Long,
        to: Long
    ): Flow<List<DeviceUpdate>> {
        return kwiatonomousDb.deviceUpdateDao().getDeviceUpdatesByDate(deviceId, from, to)
            .map { deviceUpdates ->
                deviceUpdates.map { it.toDeviceUpdate() }
            }
    }

    override suspend fun saveFetchedDeviceUpdates(
        deviceId: String,
        deviceUpdates: List<DeviceUpdateEntity>
    ) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceUpdateDao().removeAllDeviceUpdates(deviceId)
            kwiatonomousDb.deviceUpdateDao().addDeviceUpdates(deviceUpdates)
        }
    }
}