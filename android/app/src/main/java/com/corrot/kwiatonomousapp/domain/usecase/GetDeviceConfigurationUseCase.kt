package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceConfigurationUseCase @Inject constructor(
    private val deviceConfigurationRepository: DeviceConfigurationRepository
) {
    fun execute(deviceId: String): Flow<Result<DeviceConfiguration>> = flow {
        try {
            emit(Result.Loading)
            val deviceConfiguration = deviceConfigurationRepository
                .getDeviceConfigurationByDeviceId(deviceId)
                .toDeviceConfiguration()
            emit(Result.Success(deviceConfiguration))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}