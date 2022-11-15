package com.corrot.kwiatonomousapp.domain.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.common.Constants.LOW_BATTERY_VOLTAGE_THRESHOLD
import com.corrot.kwiatonomousapp.common.Constants.MAX_TIME_DIFF_HOURS
import com.corrot.kwiatonomousapp.common.Result.*
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.AddDeviceEventUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@HiltWorker
class DeviceBatteryInfoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationsManager: NotificationsManager,
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
    private val addDeviceEventUseCase: AddDeviceEventUseCase,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val DEVICE_BATTERY_WORK_TAG = "DEVICE_BATTERY_WORK_TAG"
        const val DEVICE_BATTERY_WORK_NAME = "DEVICE_BATTERY_WORK_NAME"
    }

    override suspend fun doWork(): Result {
        try {
            if (!authManager.checkIfLoggedIn()) {
                Timber.e("User not logged in")
                throw Exception("User not logged in")
            }
            userRepository.getCurrentUserFromDatabase().first()?.let { user ->
                user.devices.forEach { userDevice ->

                    if (!userDevice.notificationsOn) {
                        return@forEach
                    }

                    val deviceUpdate = deviceUpdateRepository
                        .fetchAllDeviceUpdates(userDevice.deviceId, 1)
                        .first()
                        .toDeviceUpdate()

                    val hoursSinceLastUpdate = ChronoUnit.HOURS.between(deviceUpdate.updateTime, LocalDateTime.now())

                    if (deviceUpdate.batteryVoltage < LOW_BATTERY_VOLTAGE_THRESHOLD && hoursSinceLastUpdate < MAX_TIME_DIFF_HOURS) {
                        notificationsManager.sendBatteryNotification(
                            context = applicationContext,
                            deviceName = userDevice.deviceName,
                            notificationId = userDevice.deviceId.hashCode()
                        )
                        addLowBatteryEvent(
                            deviceId = userDevice.deviceId,
                            batteryVoltage = deviceUpdate.batteryVoltage,
                            batteryLevel = deviceUpdate.batteryLevel
                        )
                    }
                }
            }

            return Result.success()

        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            return if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun addLowBatteryEvent(
        deviceId: String,
        batteryVoltage: Float,
        batteryLevel: Int,
    ) {

        val deviceEvent = DeviceEvent.LowBattery(
            deviceId = deviceId,
            timestamp = LocalDateTime.now(),
            batteryVoltage = batteryVoltage,
            batteryLevel = batteryLevel
        )
        addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
            when (ret) {
                is Loading -> Timber.d("addDeviceEventUseCase (Low battery) loading...")
                is Success -> Timber.d("addDeviceEventUseCase (Low battery) success")
                is Error -> Timber.e("addDeviceEventUseCase (Low battery) error (${ret.throwable.message})")
            }
        }
    }
}
