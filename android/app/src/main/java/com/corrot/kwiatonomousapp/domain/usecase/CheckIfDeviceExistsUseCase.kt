package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class CheckIfDeviceExistsUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val userDeviceRepository: UserDeviceRepository
) {
    fun execute(deviceId: String): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            // Check if device is in remote database
            deviceRepository.fetchDeviceById(deviceId) // just fetch, if failed it will throw

            // Check if device is in local database
            if (userDeviceRepository.getUserDevice(deviceId).firstOrNull() != null) {
                throw Exception("Device with ID \"$deviceId\" is already added")
            }

            emit(Result.Success(true))
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emit(Result.Success(false))
            } else {
                emit(Result.Error(e))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}