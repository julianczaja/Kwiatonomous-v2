package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class GetDeviceNextWateringUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: String): Flow<Result<LocalDateTime>> = flow {
        try {
            emit(Result.Loading)
            val nextWatering = deviceRepository.fetchNextWateringByDeviceId(deviceId)
            emit(Result.Success(nextWatering))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}