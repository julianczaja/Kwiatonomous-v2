package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateDeviceNextWateringUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    @Throws(Exception::class)
    suspend fun execute(deviceId: String, nextWatering: LocalDateTime) {
        deviceRepository.updateNextWateringByDeviceId(deviceId, nextWatering)
    }
}