package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserDeviceUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userDevice: UserDevice) = flow {
        emit(Result.Loading())
        try {
            val user = userRepository.getCurrentUserFromDatabase().first()
                ?: throw Exception("There is no logged in user")

            user.devices.find { it.deviceId == userDevice.deviceId }
                ?: throw Exception("User doesn't have this device")

            val newDevices = user.devices.map {
                if (it.deviceId == userDevice.deviceId) {
                    return@map userDevice
                } else {
                    return@map it
                }
            }
            userRepository.updateCurrentUserAddedDevices(newDevices) // remote
            userRepository.updateUser(user.copy(devices = newDevices)) // local
            emit(Result.Success(userDevice))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}