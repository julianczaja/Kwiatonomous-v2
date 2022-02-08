package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.networkBoundResource
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.toDeviceUpdateEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceUpdatesByDateUseCase @Inject constructor(
    private val deviceUpdateRepository: DeviceUpdateRepository
) {
    fun execute(deviceId: String, from: Long, to: Long): Flow<Result<List<DeviceUpdate>>> {
        return networkBoundResource(
            query = {
                deviceUpdateRepository.getDeviceUpdatesByDateFromDatabase(deviceId, from, to)
            },
            fetch = {
                deviceUpdateRepository.fetchDeviceUpdatesByDate(deviceId, from, to)
                    .map { it.toDeviceUpdate() }
            },
            saveFetchResult = { ret ->
                deviceUpdateRepository.saveFetchedDeviceUpdates(ret.map { it.toDeviceUpdateEntity() })
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