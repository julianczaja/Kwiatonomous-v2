package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateDeviceNextWateringUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: String, nextWatering: LocalDateTime) = flow {
        try {
            emit(Result.Loading())
            deviceRepository.updateNextWateringByDeviceId(deviceId, nextWatering)
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}