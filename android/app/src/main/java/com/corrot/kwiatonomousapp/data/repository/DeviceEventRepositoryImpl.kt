package com.corrot.kwiatonomousapp.data.repository

import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEventEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceEvent
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceEventDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceEventRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase,
) : DeviceEventRepository {

    override suspend fun fetchAllDeviceEvents(deviceId: String) =
        kwiatonomousApi.getAllDeviceEvents(deviceId)

    override suspend fun fetchAllDeviceEvents(deviceId: String, limit: Int) =
        kwiatonomousApi.getAllDeviceEvents(deviceId, limit)

    override suspend fun fetchDeviceEventsByDate(deviceId: String, from: Long, to: Long) =
        kwiatonomousApi.getDeviceEventsByDate(deviceId, from, to)

    override suspend fun addNewDeviceEventToBackend(
        deviceId: String,
        deviceEventDto: DeviceEventDto,
    ) =
        kwiatonomousApi.addNewDeviceEvent(deviceId, deviceEventDto)

    override suspend fun removeDeviceEventFromBackend(
        deviceId: String,
        deviceEventDto: DeviceEventDto,
    ) =
        kwiatonomousApi.removeDeviceEvent(deviceId, deviceEventDto)

    override fun getAllDeviceEventsFromDatabase(deviceId: String) =
        kwiatonomousDb.deviceEventDao().getAll(deviceId).map { deviceEvents ->
            deviceEvents.map { it.toDeviceEvent() }
        }

    override fun getAllDeviceEventsFromDatabase(deviceId: String, limit: Int) =
        kwiatonomousDb.deviceEventDao().getAll(deviceId, limit).map { deviceEvents ->
            deviceEvents.map { it.toDeviceEvent() }
        }

    override fun getDeviceEventsByDateFromDatabase(deviceId: String, from: Long, to: Long) =
        kwiatonomousDb.deviceEventDao().getAllByDate(deviceId, from, to)
            .map { deviceEvents ->
                deviceEvents.map { it.toDeviceEvent() }
            }

    override suspend fun addNewDeviceEventToDatabase(deviceEventEntity: DeviceEventEntity) {
        kwiatonomousDb.deviceEventDao().insertOrUpdate(deviceEventEntity)
    }

    override suspend fun removeDeviceEventFromDatabase(deviceId: String, timestamp: Long) {
        kwiatonomousDb.deviceEventDao().remove(deviceId, timestamp)
    }

    override suspend fun saveFetchedDeviceEvents(deviceEvents: List<DeviceEventEntity>) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceEventDao().insertOrUpdate(deviceEvents)
        }
    }

    override suspend fun removeAll() {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceEventDao().removeAll()
        }
    }
}