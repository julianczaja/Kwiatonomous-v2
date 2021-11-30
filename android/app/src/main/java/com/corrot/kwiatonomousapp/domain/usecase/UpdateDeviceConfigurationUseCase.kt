package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import javax.inject.Inject

class UpdateDeviceConfigurationUseCase @Inject constructor(
    private val deviceConfigurationRepository: DeviceConfigurationRepository
) {
    @Throws(Exception::class)
    suspend fun execute(deviceId: String, configuration: DeviceConfiguration) {
        if (deviceId.isBlank()) {
            throw Exception("Invalid device ID ($deviceId)!")
        }
        // TODO: Add more validation
        deviceConfigurationRepository.updateDeviceConfiguration(deviceId, configuration)
    }
}