package com.corrot.kwiatonomousapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationsManager {
    companion object {
        const val DEVICE_BATTERY_CHANNEL_NAME = "Devices low battery alert"
        const val DEVICE_BATTERY_CHANNEL_ID = "BATTERY_CHANNEL_ID"
    }

    fun init(context: Context) {
        createBatteryNotificationChannel(context)
    }

    private fun createBatteryNotificationChannel(context: Context) {
        with(context as Activity) {
            val channel = NotificationChannel(
                DEVICE_BATTERY_CHANNEL_ID,
                DEVICE_BATTERY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendBatteryNotification(context: Context, deviceName: String, notificationId: Int) {
        val notification =
            NotificationCompat.Builder(context, DEVICE_BATTERY_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_v1)
                .setContentTitle(context.getString(R.string.low_battery_notification_title))
                .setContentText(
                    context.getString(R.string.low_battery_notification_content)
                        .format(deviceName)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }
}