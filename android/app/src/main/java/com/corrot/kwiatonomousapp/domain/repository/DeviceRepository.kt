package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import java.time.LocalDateTime

interface DeviceRepository {

    suspend fun getDevices(): List<DeviceDto>

    suspend fun getDeviceById(id: String): DeviceDto

    suspend fun getNextWateringByDeviceId(id: String): LocalDateTime

    suspend fun updateNextWateringByDeviceId(id: String, nextWatering: LocalDateTime)
}