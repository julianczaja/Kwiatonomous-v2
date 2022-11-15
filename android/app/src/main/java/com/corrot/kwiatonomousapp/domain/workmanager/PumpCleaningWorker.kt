package com.corrot.kwiatonomousapp.domain.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.common.Constants.MAX_TIME_DIFF_HOURS
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.domain.NotificationsManager
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@HiltWorker
class PumpCleaningWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationsManager: NotificationsManager,
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val PUMP_CLEANING_WORK_TAG = "PUMP_CLEANING_WORK_TAG"
        const val PUMP_CLEANING_WORK_NAME = "PUMP_CLEANING_WORK_NAME"
        const val PUMP_CLEANING_INTERVAL_HOURS = 24 * 30 // once per month
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

                    val device = deviceRepository.fetchDeviceById(userDevice.deviceId).toDevice()
                    val hoursSinceLastUpdate = ChronoUnit.HOURS.between(device.lastUpdate, LocalDateTime.now())
                    val hoursSinceLastPumpCleaning = ChronoUnit.HOURS.between(device.lastPumpCleaning, LocalDateTime.now())

                    if (hoursSinceLastPumpCleaning >= PUMP_CLEANING_INTERVAL_HOURS && hoursSinceLastUpdate < MAX_TIME_DIFF_HOURS) {
                        notificationsManager.sendPumpCleaningReminderNotification(
                            context = applicationContext,
                            deviceId = userDevice.deviceId,
                            deviceName = userDevice.deviceName,
                            notificationId = userDevice.deviceId.hashCode()
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
}
