package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDevicesWidgetDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(): Flow<Map<UserDevice, DeviceUpdate>> =
        userRepository.getCurrentUserFromDatabase()
            .filterNotNull()
            .flatMapLatest { user ->
                val devicesFlows = user.devices.map { device ->
                    deviceUpdateRepository.getAllDeviceUpdatesFromDatabase(device.deviceId, limit = 1)
                        .map { updates -> device to updates.firstOrNull() }
                }

                combine(devicesFlows) { pairs ->
                    pairs
                        .filter { it.second != null }
                        .associate { it.first to it.second!! }
                }
            }
}
