package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto

interface DeviceRepository {

    suspend fun getDevices(): List<DeviceDto>

    suspend fun getDeviceById(id: String): DeviceDto
}