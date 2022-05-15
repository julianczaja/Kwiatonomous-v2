package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Constants.DEVICE_INACTIVE_TIME_SECONDS
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class GetUserDevicesWithLastUpdatesUseCase @Inject constructor(
    private val userDeviceRepository: UserDeviceRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
) {
    fun execute() = flow {
        userDeviceRepository.getUserDevices()
            .map { userDevices ->
                val userDevicesWithEmptyLastUpdates = userDevices.map { Pair(it, null) }
                emit(userDevicesWithEmptyLastUpdates)

                val userDevicesWithLastUpdates = userDevicesWithEmptyLastUpdates.map {
                    val userDevice = it.first
                    val lastUpdate = deviceUpdateRepository
                        .fetchAllDeviceUpdates(userDevice.deviceId, 1)
                        .firstOrNull()
                        ?.toDeviceUpdate()

                    Pair(userDevice, if (isDeviceActive(lastUpdate)) lastUpdate else null)
                }
                emit(userDevicesWithLastUpdates)
            }
            .collect()
    }

    private fun isDeviceActive(lastUpdate: DeviceUpdate?): Boolean {
        val lastUpdateTime = lastUpdate?.updateTime
        val currentTime = LocalDateTime.now().toLong()
        return lastUpdateTime != null && (currentTime - lastUpdateTime.toLong()) < DEVICE_INACTIVE_TIME_SECONDS
    }
}