package com.corrot.kwiatonomousapp.domain.usecase

import android.util.Log
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeviceUpdatesByDateUseCase @Inject constructor(
    private val deviceUpdateRepository: DeviceUpdateRepository
) {
    fun execute(deviceId: String, from: Long, to: Long): Flow<Result<List<DeviceUpdate>>> = flow {
        Log.i("GetDeviceUpdatesByDateUseCase", "execute: deviceId=$deviceId, from=$from, to=$to")
        try {
            emit(Result.Loading)
            val deviceUpdates = deviceUpdateRepository
                .getDeviceUpdatesByDate(deviceId, from, to)
                .map { it.toDeviceUpdate() }
            Log.i(
                "GetDeviceUpdatesByDateUseCase",
                "execute: Success! Found ${deviceUpdates.size} records!"
            )
            emit(Result.Success(deviceUpdates))
        } catch (e: Exception) {
            Log.i("GetDeviceUpdatesByDateUseCase", "execute: Failed with error: $e")
            emit(Result.Error(e))
        }
    }
}