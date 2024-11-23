package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateDeviceLastPumpCleaningUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: String, lastPumpCleaning: LocalDateTime) = flow {
        try {
            emit(Result.Loading())
            // TODO: Update locally too
            deviceRepository.updateLastPumpCleaningByDeviceIdRemote(deviceId, lastPumpCleaning)
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}