package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import javax.inject.Inject

class DeviceConfigurationRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi
) : DeviceConfigurationRepository {

    override suspend fun getDeviceConfigurationByDeviceId(id: String): DeviceConfigurationDto {
        return kwiatonomousApi.getDeviceConfigurationByDeviceId(id)
    }

    override suspend fun updateDeviceConfiguration(
        id: String,
        configuration: DeviceConfigurationDto
    ) {
        return kwiatonomousApi.updateDeviceConfiguration(id, configuration)
    }
}