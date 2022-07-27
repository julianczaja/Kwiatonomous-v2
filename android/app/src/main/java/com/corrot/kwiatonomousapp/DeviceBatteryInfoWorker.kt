package com.corrot.kwiatonomousapp

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.common.Constants.LOW_BATTERY_VOLTAGE_THRESHOLD
import com.corrot.kwiatonomousapp.common.Constants.MAX_TIME_DIFF_HOURS
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
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
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val DEVICE_BATTERY_WORK_TAG = "DEVICE_BATTERY_WORK_TAG"
        const val DEVICE_BATTERY_WORK_NAME = "DEVICE_BATTERY_WORK_NAME"
    }

    override suspend fun doWork(): Result {
        try {
            if (!authManager.checkIfLoggedIn()) {
                Timber.e("User not logged in")
                return Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            return Result.retry()
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

                val timeDiffHours =
                    ChronoUnit.HOURS.between(deviceUpdate.updateTime, LocalDateTime.now())

                if (deviceUpdate.batteryVoltage < LOW_BATTERY_VOLTAGE_THRESHOLD
                    && timeDiffHours < MAX_TIME_DIFF_HOURS
                ) {
                    notificationsManager.sendBatteryNotification(
                        context = applicationContext,
                        deviceName = userDevice.deviceName,
                        notificationId = userDevice.deviceId.hashCode()
                    )
                }
            }
        }

        return Result.success()
    }
}
