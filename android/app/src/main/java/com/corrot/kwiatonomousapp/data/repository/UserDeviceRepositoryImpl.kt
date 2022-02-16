package com.corrot.kwiatonomousapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.corrot.kwiatonomousapp.data.local.database.KwiatonomousDatabase
import com.corrot.kwiatonomousapp.data.local.database.entity.toUserDevice
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.model.toUserDeviceEntity
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDeviceRepositoryImpl @Inject constructor(
    private val kwiatonomousDb: KwiatonomousDatabase
) : UserDeviceRepository {

    override fun getUserDevice(deviceId: String): Flow<UserDevice> {
        return kwiatonomousDb.userDeviceDao().getUserDevice(deviceId)
            .map { it.toUserDevice() }
            .catch { t ->
                Log.e("UserDeviceRepositoryImpl", "getUserDevice: $t")
            }
    }

    override fun getUserDevices(): Flow<List<UserDevice>> {
        return kwiatonomousDb.userDeviceDao().getAllUserDevices().map { devices ->
            devices.map { it.toUserDevice() }
        }
    }

    override suspend fun addUserDevice(device: UserDevice) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDeviceDao().addUserDevice(device.toUserDeviceEntity())
        }
    }

    override suspend fun updateUserDevice(device: UserDevice) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDeviceDao().updateUserDevice(device.toUserDeviceEntity())
        }
    }

    override suspend fun removeUserDevice(device: UserDevice) {
        kwiatonomousDb.withTransaction {
            kwiatonomousDb.userDeviceDao().removeUserDevice(device.deviceId)
        }
    }
}