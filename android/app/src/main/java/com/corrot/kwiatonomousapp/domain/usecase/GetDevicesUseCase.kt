package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(): Flow<Result<List<Device>>> = flow {
        try {
            emit(Result.Loading)
            val devices = deviceRepository.getDevices().map { it.toDevice() }
            emit(Result.Success(devices))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}