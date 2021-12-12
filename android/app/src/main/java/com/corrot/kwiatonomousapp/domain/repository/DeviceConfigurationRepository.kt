package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto

interface DeviceConfigurationRepository {

    suspend fun getDeviceConfigurationByDeviceId(id: String): DeviceConfigurationDto

    suspend fun updateDeviceConfiguration(id: String, configuration: DeviceConfigurationDto)
}