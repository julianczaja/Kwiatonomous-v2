package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteUserDeviceUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(userDevice: UserDevice) = flow {
        emit(Result.Loading())
        try {
            val user = userRepository.getCurrentUserFromDatabase().first()
                ?: throw Exception("There is no logged in user")

            val newUserDevices = user.devices.toMutableList()
            val isRemoved = newUserDevices.remove(userDevice)

            if (!isRemoved) throw Exception("Device not removed")

            userRepository.updateCurrentUserAddedDevices(newUserDevices) // remote
            userRepository.updateUser(user.copy(devices = newUserDevices)) // local
            emit(Result.Success(null))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}