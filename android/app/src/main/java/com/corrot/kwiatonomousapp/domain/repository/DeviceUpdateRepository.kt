package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto

interface DeviceUpdateRepository {

    suspend fun getAllDeviceUpdates(id: String): List<DeviceUpdateDto>

    suspend fun getAllDeviceUpdates(id: String, limit: Int): List<DeviceUpdateDto>

    suspend fun getDeviceUpdatesByDate(id: String, from: Long, to: Long): List<DeviceUpdateDto>
}