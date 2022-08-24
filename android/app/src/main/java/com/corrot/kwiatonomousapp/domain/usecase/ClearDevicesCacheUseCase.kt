package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import javax.inject.Inject

class ClearDevicesCacheUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val updateRepository: DeviceUpdateRepository,
    private val configurationRepository: DeviceConfigurationRepository,
    private val deviceEventRepository: DeviceEventRepository,
) {
    suspend fun execute(
        clearDevices: Boolean = true,
        clearUpdates: Boolean = true,
        clearConfigurations: Boolean = true,
        clearEvents: Boolean = true,
    ) {
        if (clearDevices) {
            deviceRepository.removeAll()
        }
        if (clearUpdates) {
            updateRepository.removeAll()
        }
        if (clearConfigurations) {
            configurationRepository.removeAll()
        }
        if (clearEvents) {
            deviceEventRepository.removeAll()
        }
    }
}