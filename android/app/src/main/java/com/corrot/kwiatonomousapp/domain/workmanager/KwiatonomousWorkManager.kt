package com.corrot.kwiatonomousapp.domain.workmanager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.corrot.kwiatonomousapp.common.getMinutesUntilLocalTime
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class KwiatonomousWorkManager(@ApplicationContext private val applicationContext: Context) {

    fun setupWorkManager(notificationsSettings: NotificationsSettings) {
        val workManager = WorkManager.getInstance(applicationContext)

        if (notificationsSettings.notificationsOn) {
            setupBatteryWork(workManager, notificationsSettings.notificationsTime)
            setupPumpCleaningReminderWork(workManager, notificationsSettings.notificationsTime)
        } else {
            workManager.cancelAllWork()
        }
    }

    private fun setupBatteryWork(workManager: WorkManager, notificationsTime: LocalTime) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<DeviceBatteryInfoWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(DeviceBatteryInfoWorker.DEVICE_BATTERY_WORK_TAG)
            .setInitialDelay(getMinutesUntilLocalTime(toTime = notificationsTime), TimeUnit.MINUTES)


        workManager.enqueueUniquePeriodicWork(
            DeviceBatteryInfoWorker.DEVICE_BATTERY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest.build()
        )
    }

    private fun setupPumpCleaningReminderWork(
        workManager: WorkManager,
        notificationsTime: LocalTime
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<PumpCleaningWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(PumpCleaningWorker.PUMP_CLEANING_WORK_TAG)
            .setInitialDelay(getMinutesUntilLocalTime(toTime = notificationsTime), TimeUnit.MINUTES)

        workManager.enqueueUniquePeriodicWork(
            PumpCleaningWorker.PUMP_CLEANING_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest.build()
        )
    }

    fun enqueueDevicesWidgetUpdate(force: Boolean = false) {
        val policy = when {
            force -> ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            else -> ExistingPeriodicWorkPolicy.KEEP
        }
        val interval = Duration.ofMinutes(30)
        val workRequest = PeriodicWorkRequestBuilder<DeviceWidgetWorker>(interval)
            .addTag(DeviceWidgetWorker.DEVICE_WIDGET_WORK_TAG)

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                uniqueWorkName = DeviceWidgetWorker.DEVICE_WIDGET_WORK_NAME,
                existingPeriodicWorkPolicy = policy,
                request = workRequest.build()
            )
    }

    fun cancelDevicesWidgetUpdates() {
        WorkManager.getInstance(applicationContext)
            .cancelUniqueWork(DeviceWidgetWorker.DEVICE_WIDGET_WORK_NAME)
    }
}