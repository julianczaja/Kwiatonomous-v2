package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.networkBoundResource
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.toDeviceConfigurationEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceConfigurationUseCase @Inject constructor(
    private val deviceConfigurationRepository: DeviceConfigurationRepository
) {
    fun execute(deviceId: String): Flow<Result<DeviceConfiguration?>> {
        return networkBoundResource(
            query = {
                deviceConfigurationRepository.getDeviceConfigurationFromDatabase(deviceId)
            },
            fetch = {
                deviceConfigurationRepository.fetchDeviceConfigurationByDeviceId(deviceId)
                    .toDeviceConfiguration()
            },
            saveFetchResult = { ret ->
                deviceConfigurationRepository.saveFetchedDeviceConfiguration(
                    ret.toDeviceConfigurationEntity()
                )
            },
            shouldFetch = { ret ->
                // TODO: Check if data is old or not
                true
            }
        )
    }
}