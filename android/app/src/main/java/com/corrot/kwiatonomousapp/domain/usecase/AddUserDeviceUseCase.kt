package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val userRepository: UserRepository
) {
    fun execute(userDevice: UserDevice) = flow {
        emit(Result.Loading())
        try {
            val user = userRepository.getCurrentUserFromDatabase().first()
                ?: throw Exception("There is no logged in user")

            if (user.devices.find { it.deviceId == userDevice.deviceId } != null) {
                throw Exception("Device with ID \"${userDevice.deviceId}\" is already added")
            }

            // Check if device is in remote database
            deviceRepository.fetchDeviceById(userDevice.deviceId) // just fetch, if failed it will throw

            val newDevices = user.devices.toMutableList()
            newDevices.add(userDevice)

            userRepository.updateCurrentUserAddedDevices(newDevices) // remote
            userRepository.updateUser(user.copy(devices = newDevices)) // local
            emit(Result.Success(userDevice))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}