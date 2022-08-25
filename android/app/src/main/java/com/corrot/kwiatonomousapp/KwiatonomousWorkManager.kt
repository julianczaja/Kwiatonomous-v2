package com.corrot.kwiatonomousapp

import android.content.Context
import androidx.work.*
import com.corrot.kwiatonomousapp.common.getMinutesUntilLocalTime
import com.corrot.kwiatonomousapp.domain.model.NotificationsSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class KwiatonomousWorkManager(@ApplicationContext private val applicationContext: Context) {

    fun setupWorkManager(notificationsSettings: NotificationsSettings) {
        val workManager = WorkManager.getInstance(applicationContext)
        if (notificationsSettings.notificationsOn) {
            setupBatteryWork(workManager, notificationsSettings.notificationsTime)
        } else {
            workManager.cancelAllWork()
        }
    }

    private fun setupBatteryWork(workManager: WorkManager, notificationsTime: LocalTime) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val work = PeriodicWorkRequestBuilder<DeviceBatteryInfoWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag(DeviceBatteryInfoWorker.DEVICE_BATTERY_WORK_TAG)
            .setInitialDelay(getMinutesUntilLocalTime(notificationsTime), TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DeviceBatteryInfoWorker.DEVICE_BATTERY_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
    }
}