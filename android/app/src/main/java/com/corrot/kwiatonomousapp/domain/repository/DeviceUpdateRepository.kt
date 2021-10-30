package com.corrot.kwiatonomousapp.domain.repository

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto

interface DeviceUpdateRepository {

    suspend fun getDeviceUpdatesByDeviceId(id: String): List<DeviceUpdateDto>

    suspend fun getDeviceUpdatesByDeviceId(id: String, limit: Int): List<DeviceUpdateDto>
}