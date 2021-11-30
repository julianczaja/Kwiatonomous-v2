package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration

interface DeviceConfigurationRepository {

    suspend fun getDeviceConfigurationByDeviceId(id: String): DeviceConfigurationDto

    suspend fun updateDeviceConfiguration(id: String, configuration: DeviceConfiguration)
}