package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.networkBoundResource
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.toDeviceEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    fun execute(deviceId: String): Flow<Result<Device>> {
        return networkBoundResource(
            query = {
                deviceRepository.getDeviceFromDatabase(deviceId)
            },
            fetch = {
                deviceRepository.fetchDeviceById(deviceId).toDevice()
            },
            saveFetchResult = { ret ->
                deviceRepository.saveFetchedDevice(ret.toDeviceEntity())
            },
            shouldFetch = { ret ->
                // TODO: Check if data is old or not
                true
            }
        )
    }
}