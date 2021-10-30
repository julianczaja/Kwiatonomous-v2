package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val kwiatonomousApi: KwiatonomousApi
) : DeviceRepository {

    override suspend fun getDevices(): List<DeviceDto> {
        return kwiatonomousApi.getDevices()
    }

    override suspend fun getDeviceById(id: String): DeviceDto {
        return kwiatonomousApi.getDeviceById(id)
    }
}