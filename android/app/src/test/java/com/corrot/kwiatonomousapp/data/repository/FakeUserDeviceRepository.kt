package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import kotlinx.coroutines.flow.flow

class FakeUserDeviceRepository : UserDeviceRepository {

    private val databaseUserDevices = mutableListOf(
        UserDevice("id1", "Name1", 1, false),
        UserDevice("id2", "Name2", 2, false),
    )

    override fun getUserDevice(deviceId: String) = flow {
        databaseUserDevices.find { it.deviceId == deviceId }?.let {
            emit(it)
        }
    }

    override fun getUserDevices() = flow {
        emit(databaseUserDevices)
    }

    override suspend fun addUserDevice(device: UserDevice) {
        databaseUserDevices.add(device)
    }

    override suspend fun updateUserDevice(device: UserDevice) {
        databaseUserDevices.forEachIndexed { index, userDevice ->
            if (userDevice.deviceId == device.deviceId) {
                databaseUserDevices[index] = device
                return
            }
        }
        throw Exception("Can't find device")
    }

    override suspend fun removeUserDevice(device: UserDevice) {
        databaseUserDevices.find { it.deviceId == device.deviceId }.let { found ->
            if (found != null) {
                databaseUserDevices.remove(found)
            } else {
                throw Exception("Can't find device")
            }
        }
    }
}