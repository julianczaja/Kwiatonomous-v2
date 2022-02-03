package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.domain.model.UserDevice
import kotlinx.coroutines.flow.Flow

interface UserDeviceRepository {

    fun getUserDevice(deviceId: String): Flow<UserDevice>

    fun getUserDevices(): Flow<List<UserDevice>>

    suspend fun addUserDevice(device: UserDevice)

    suspend fun removeUserDevice(device: UserDevice)
}