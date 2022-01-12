package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import kotlinx.coroutines.flow.Flow

interface DeviceConfigurationRepository {

    // Remote
    suspend fun fetchDeviceConfigurationByDeviceId(deviceId: String): DeviceConfigurationDto

    suspend fun updateDeviceConfiguration(id: String, configuration: DeviceConfigurationDto)

    // Local
    fun getDeviceConfigurationFromDatabase(deviceId: String): Flow<DeviceConfiguration?>

    suspend fun saveFetchedDeviceConfiguration(deviceConfiguration: DeviceConfigurationEntity)
}