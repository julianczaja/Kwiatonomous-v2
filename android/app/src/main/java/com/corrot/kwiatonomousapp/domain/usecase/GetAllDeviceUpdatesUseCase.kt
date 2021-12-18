package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllDeviceUpdatesUseCase @Inject constructor(
    private val deviceUpdateRepository: DeviceUpdateRepository
) {
    fun execute(deviceId: String, limit: Int): Flow<Result<List<DeviceUpdate>>> = flow {
        try {
            emit(Result.Loading)
            val deviceUpdates = deviceUpdateRepository.getAllDeviceUpdates(deviceId, limit)
                .map { it.toDeviceUpdate() }
            emit(Result.Success(deviceUpdates))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}