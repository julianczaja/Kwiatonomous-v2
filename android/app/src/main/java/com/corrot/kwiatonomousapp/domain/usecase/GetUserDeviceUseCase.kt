package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserDeviceUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun execute(deviceId: String) = flow {
        emit(Result.Loading())
        try {
            userRepository.getCurrentUserFromDatabase().collect { user ->
                if (user == null) throw Exception("There is no logged in user")

                val userDevice = user.devices.find { it.deviceId == deviceId }
                    ?: throw Exception("User doesn't have this device")
                emit(Result.Success(userDevice))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}