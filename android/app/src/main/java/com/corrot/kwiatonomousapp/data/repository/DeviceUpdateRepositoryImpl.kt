package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import javax.inject.Inject

class DeviceUpdateRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi
) : DeviceUpdateRepository {

    override suspend fun getAllDeviceUpdates(id: String): List<DeviceUpdateDto> {
        return kwiatonomousApi.getAllDeviceUpdates(id)
    }

    override suspend fun getAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto> {
        return kwiatonomousApi.getAllDeviceUpdates(id, limit)
    }

    override suspend fun getDeviceUpdatesByDate(
        id: String,
        from: Long,
        to: Long
    ): List<DeviceUpdateDto> {
        return kwiatonomousApi.getDeviceUpdatesByDate(id, from, to)
    }
}