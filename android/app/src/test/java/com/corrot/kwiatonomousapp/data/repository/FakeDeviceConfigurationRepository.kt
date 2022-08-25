package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.toDeviceConfiguration
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.flow
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.HttpException
import retrofit2.Response

class FakeDeviceConfigurationRepository : DeviceConfigurationRepository {

    private val backendDevicesConfigurations = mutableListOf(
        DeviceConfigurationDto(30, 1, 1, 2, 100, "10:00"),
        DeviceConfigurationDto(15, 1, 1, 1, 250, "13:30"),
    )

    private val databaseDevicesConfigurations = mutableListOf<DeviceConfigurationEntity>()

    override suspend fun fetchDeviceConfigurationByDeviceId(deviceId: String): DeviceConfigurationDto {
        return when (deviceId) {
            "id1" -> backendDevicesConfigurations[0]
            "id2" -> backendDevicesConfigurations[1]
            else -> throw HttpException(Response.error<String>(404, EMPTY_RESPONSE))
        }
    }

    override suspend fun updateDeviceConfiguration(
        id: String,
        configuration: DeviceConfigurationDto
    ) {
        when (id) {
            "id1" -> backendDevicesConfigurations[0] = configuration
            "id2" -> backendDevicesConfigurations[1] = configuration
            else -> throw HttpException(Response.error<String>(404, EMPTY_RESPONSE))
        }
    }

    override fun getDeviceConfigurationFromDatabase(deviceId: String) = flow {
        val found = databaseDevicesConfigurations.findLast { deviceId == it.deviceId }
            ?.toDeviceConfiguration()
        found?.let {
            emit(it)
        }
    }

    override suspend fun saveFetchedDeviceConfiguration(deviceConfiguration: DeviceConfigurationEntity) {
        databaseDevicesConfigurations.add(deviceConfiguration)
    }

    override suspend fun removeAll() {
        backendDevicesConfigurations.clear()
    }

}