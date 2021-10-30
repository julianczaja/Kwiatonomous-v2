package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import javax.inject.Inject

class DeviceUpdateRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi
) : DeviceUpdateRepository {

    override suspend fun getDeviceUpdatesByDeviceId(id: String): List<DeviceUpdateDto> {
        return kwiatonomousApi.getDeviceUpdatesByDeviceId(id)
    }

    override suspend fun getDeviceUpdatesByDeviceId(id: String, limit: Int): List<DeviceUpdateDto> {
        return kwiatonomousApi.getDeviceUpdatesByDeviceId(id, limit)
    }
}