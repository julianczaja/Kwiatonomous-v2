package com.corrot.kwiatonomousapp.data.repository

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import java.time.LocalDateTime
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

    override suspend fun getNextWateringByDeviceId(id: String): LocalDateTime {
        return kwiatonomousApi.getNextWateringByDeviceId(id).toLocalDateTime()
    }

    override suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime) {
        kwiatonomousApi.updateNextWateringByDeviceId(id, nextWatering.toLong())
    }
}