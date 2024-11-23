package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddDeviceEventUseCase @Inject constructor(
    private val eventRepository: DeviceEventRepository,
) {
    fun execute(deviceEvent: DeviceEvent) = flow {
        emit(Result.Loading())
        try {
            with(eventRepository) {
                addNewDeviceEventToBackend(deviceEvent.deviceId, deviceEvent.toDeviceEventDto())
                addNewDeviceEventToDatabase(deviceEvent.toDeviceEventEntity())
            }
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}