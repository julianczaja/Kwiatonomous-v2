package com.corrot.kwiatonomousapp.presentation

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.AuthManager
import com.corrot.kwiatonomousapp.R
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
class BatteryInfoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
) : CoroutineWorker(appContext, workerParams) {

    private fun sendNotification(
        deviceName: String,
        notificationId: Int,
    ) {
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.low_battery_notification_title))
            .setContentText(applicationContext.getString(R.string.low_battery_notification_content)
                .format(deviceName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, notification)
        }
    }

    override suspend fun doWork(): Result {
        Timber.d("doWork")

        if (!authManager.checkIfLoggedIn()) {
            Timber.e("User not logged in")
            return Result.failure()
        }

        val user = userRepository.getCurrentUserFromDatabase().first()

        when {
            user != null -> {
                user.devices.forEachIndexed { index, userDevice ->
                    val deviceUpdate = deviceUpdateRepository
                        .fetchAllDeviceUpdates(userDevice.deviceId, 1)
                        .first()
                        .toDeviceUpdate()

                    val timeDiffHours =
                        ChronoUnit.HOURS.between(deviceUpdate.updateTime, LocalDateTime.now())

                    if (deviceUpdate.batteryVoltage < LOW_BATTERY_VOLTAGE_THRESHOLD && timeDiffHours < MAX_TIME_DIFF_HOURS) {
                        sendNotification(
                            deviceName = userDevice.deviceName,
                            notificationId = index // FIXME
                        )
                    }
                }
                return Result.success()
            }
            else -> {
                return Result.failure()
            }
        }
    }
}