package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceUpdatesByDateUseCase @Inject constructor(
    private val deviceUpdateRepository: DeviceUpdateRepository
) {
    fun execute(deviceId: String, from: Long, to: Long): Flow<Result<List<DeviceUpdate>>> = flow {
        try {
            emit(Result.Loading)
            val deviceUpdates = deviceUpdateRepository
                .getDeviceUpdatesByDate(deviceId, from, to)
                .map { it.toDeviceUpdate() }
            emit(Result.Success(deviceUpdates))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}