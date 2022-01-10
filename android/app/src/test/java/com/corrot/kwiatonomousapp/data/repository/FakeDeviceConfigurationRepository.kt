package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository

class FakeDeviceConfigurationRepository : DeviceConfigurationRepository {

    private val devicesConfigurations = mutableListOf(
        DeviceConfigurationDto(30, 1, 1, 2, 100, "10:00"),
        DeviceConfigurationDto(15, 1, 1, 1, 250, "13:30"),
    )

    override suspend fun getDeviceConfigurationByDeviceId(id: String): DeviceConfigurationDto {
        return when (id) {
            "id1" -> devicesConfigurations[0]
            "id2" -> devicesConfigurations[1]
            else -> throw Exception("Can't find device")
        }
    }

    override suspend fun updateDeviceConfiguration(
        id: String,
        configuration: DeviceConfigurationDto
    ) {
        when (id) {
            "id1" -> devicesConfigurations[0] = configuration
            "id2" -> devicesConfigurations[1] = configuration
            else -> throw Exception("Can't find device")
        }
    }

}