package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteDeviceEventUseCase @Inject constructor(
    private val eventRepository: DeviceEventRepository,
) {
    suspend fun execute(deviceEvent: DeviceEvent) = flow {
        emit(Result.Loading())
        try {
            with(eventRepository) {
                removeDeviceEventFromBackend(deviceEvent.deviceId, deviceEvent.toDeviceEventDto())
                removeDeviceEventFromDatabase(deviceEvent.deviceId, deviceEvent.timestamp.toLong())
            }
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}