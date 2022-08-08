package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.networkBoundResource
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDeviceEventsUseCase @Inject constructor(
    private val deviceEventRepository: DeviceEventRepository,
) {
    fun execute(deviceId: String, limit: Int): Flow<Result<List<DeviceEvent>>> {
        return networkBoundResource(
            query = {
                deviceEventRepository.getAllDeviceEventsFromDatabase(deviceId, limit)
            },
            fetch = {
                deviceEventRepository.fetchAllDeviceEvents(deviceId, limit)
                    .map { it.toDeviceEvent(deviceId) }
            },
            saveFetchResult = { ret ->
                deviceEventRepository.saveFetchedDeviceEvents(ret.map { it.toDeviceEventEntity() })
            },
            onFetchFailed = {
                throw it
            },
            shouldFetch = { ret ->
                // TODO: Check if data is old or not
                true
            }
        )
    }
}