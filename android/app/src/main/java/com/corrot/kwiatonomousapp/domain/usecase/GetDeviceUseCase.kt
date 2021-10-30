package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: String): Flow<Result<Device>> = flow {
        try {
            emit(Result.Loading)
            val device = deviceRepository.getDeviceById(deviceId).toDevice()
            emit(Result.Success(device))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}