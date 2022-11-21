package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Constants.DEVICE_INACTIVE_TIME_SECONDS
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.model.toDeviceUpdateEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

class GetUserDevicesWithLastUpdatesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
) {
    fun execute(): Flow<Result<List<Pair<UserDevice, DeviceUpdate?>>>> = flow {
        try {
            val user = userRepository.getCurrentUserFromDatabase().firstOrNull() ?: throw Exception("There is no logged in user")
            val userDevicesWithEmptyLastUpdates = user.devices.map { userDevice ->
                val lastUpdate = deviceUpdateRepository.getAllDeviceUpdatesFromDatabase(userDevice.deviceId, 1).firstOrNull()?.first()
                Pair(userDevice, lastUpdate)
            }
            emit(Result.Loading(userDevicesWithEmptyLastUpdates))

            val fetchedUpdates = mutableListOf<DeviceUpdateEntity>()
            val userDevicesWithLastUpdates = userDevicesWithEmptyLastUpdates.map {
                val userDevice = it.first
                val lastUpdate = deviceUpdateRepository.fetchAllDeviceUpdates(userDevice.deviceId, 1).firstOrNull()?.toDeviceUpdate()
                lastUpdate?.let { update -> fetchedUpdates.add(update.toDeviceUpdateEntity()) }
                Pair(userDevice, if (isDeviceActive(lastUpdate)) lastUpdate else null)
            }
            deviceUpdateRepository.saveFetchedDeviceUpdates(fetchedUpdates)
            emit(Result.Success(userDevicesWithLastUpdates))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    private fun isDeviceActive(lastUpdate: DeviceUpdate?): Boolean {
        val lastUpdateTime = lastUpdate?.updateTime
        val currentTime = LocalDateTime.now().toLong()
        return lastUpdateTime != null && (currentTime - lastUpdateTime.toLong()) < DEVICE_INACTIVE_TIME_SECONDS
    }
}
