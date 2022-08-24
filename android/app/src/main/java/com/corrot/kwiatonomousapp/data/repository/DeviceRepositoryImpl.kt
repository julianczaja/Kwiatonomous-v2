package com.corrot.kwiatonomousapp.data.repository

import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDevice
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi,
    private val kwiatonomousDb: KwiatonomousDatabase
) : DeviceRepository {

    override suspend fun fetchDevices(): List<DeviceDto> {
        return kwiatonomousApi.getDevices()
    }

    override suspend fun fetchDeviceById(id: String): DeviceDto {
        return kwiatonomousApi.getDeviceById(id)
    }

    override suspend fun fetchNextWateringByDeviceId(id: String): LocalDateTime {
        return kwiatonomousApi.getNextWateringByDeviceId(id).toLocalDateTime()
    }

    override suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime) {
        kwiatonomousApi.updateNextWateringByDeviceId(id, nextWatering.toLong())
    }

    override fun getDeviceFromDatabase(deviceId: String) =
        kwiatonomousDb.deviceDao().getByDeviceId(deviceId)
            .map { it.toDevice() }
            // When database is empty null will be returned and `toDevice` will throw exception.
            // Let's catch it - this will also emit some kind of empty flow to notify when we call
            // `query().firstOrNull()` in networkBoundResource
            .catch { t ->
                Timber.e("getDeviceFromDatabase: $t")
            }

    override fun getDevicesFromDatabase(): Flow<List<Device>> {
        return kwiatonomousDb.deviceDao().getAll().map { devices ->
            devices.map { it.toDevice() }
        }
    }

    override suspend fun saveFetchedDevice(device: DeviceEntity) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceDao().insertOrUpdate(device)
        }
    }

    override suspend fun saveFetchedDevices(devices: List<DeviceEntity>) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceDao().insertOrUpdate(devices)
        }
    }

    override suspend fun removeAll() {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.deviceDao().removeAll()
        }
    }
}