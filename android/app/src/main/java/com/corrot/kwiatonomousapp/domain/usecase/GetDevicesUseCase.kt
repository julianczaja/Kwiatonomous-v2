package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.networkBoundResource
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.toDeviceEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(): Flow<Result<List<Device>>> {
        return networkBoundResource(
            query = {
                deviceRepository.getDevicesFromDatabase()
            },
            fetch = {
                deviceRepository.fetchDevices().map { it.toDevice() }
            },
            saveFetchResult = { ret ->
                deviceRepository.saveFetchedDevices(ret.map { it.toDeviceEntity() })
            },
            shouldFetch = { ret ->
                // TODO: Check if data is old or not
                true
            }
        )
    }
}