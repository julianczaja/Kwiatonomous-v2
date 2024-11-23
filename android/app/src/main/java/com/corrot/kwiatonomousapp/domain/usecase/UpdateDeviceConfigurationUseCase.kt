package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.toDeviceConfigurationDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateDeviceConfigurationUseCase @Inject constructor(
    private val deviceConfigurationRepository: DeviceConfigurationRepository
) {
    fun execute(deviceId: String, configuration: DeviceConfiguration) = flow {
        emit(Result.Loading())
        try {
            validateInput(deviceId, configuration)
            val configurationDto = configuration.toDeviceConfigurationDto()
            deviceConfigurationRepository.updateDeviceConfiguration(deviceId, configurationDto)
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    @Throws(Exception::class)
    private fun validateInput(deviceId: String, configuration: DeviceConfiguration) {
        // TODO: Add more validation
        if (deviceId.isBlank()) {
            throw Exception("Invalid device ID ($deviceId)!")
        }
    }
}