package com.corrot.kwiatonomousapp.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.domain.model.NotificationIntentType
import com.corrot.kwiatonomousapp.domain.model.PumpCleaningNotificationExtras

class NotificationsManager {
    companion object {
        const val DEVICE_BATTERY_CHANNEL_NAME = "Devices low battery alert"
        const val DEVICE_BATTERY_CHANNEL_ID = "BATTERY_CHANNEL_ID"
        const val PUMP_CLEANING_CHANNEL_NAME = "Pump cleaning reminder"
        const val PUMP_CLEANING_CHANNEL_ID = "BATTERY_CHANNEL_ID"

        const val EXTRA_PUMP_CLEANING_NAME = "pump_cleaning"
    }

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!isPermissionGranted(context)) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        createBatteryNotificationChannel(context)
        createPumpCleaningNotificationChannel(context)
    }

    private fun isPermissionGranted(context: Context) = ContextCompat
        .checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

    private fun createBatteryNotificationChannel(context: Context) {
        with(context as Activity) {
            val channel = NotificationChannel(
                DEVICE_BATTERY_CHANNEL_ID,
                DEVICE_BATTERY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                createNotificationChannel(channel)
            }
        }
    }

    private fun createPumpCleaningNotificationChannel(context: Context) {
        with(context as Activity) {
            val channel = NotificationChannel(
                PUMP_CLEANING_CHANNEL_ID,
                PUMP_CLEANING_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                createNotificationChannel(channel)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendBatteryNotification(context: Context, deviceName: String, notificationId: Int) {
        val notification = NotificationCompat.Builder(context, DEVICE_BATTERY_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_v1)
            .setContentTitle(context.getString(R.string.low_battery_notification_title))
            .setContentText(context.getString(R.string.low_battery_notification_content).format(deviceName))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (isPermissionGranted(context)) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendPumpCleaningReminderNotification(
        context: Context,
        deviceId: String,
        deviceName: String,
        notificationId: Int,
        isTest: Boolean = false,
    ) {
        val extras = PumpCleaningNotificationExtras(NotificationIntentType.POSTPONE, deviceId, notificationId, isTest)

        val postponeIntent = Intent(context, KwiatonomousBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_PUMP_CLEANING_NAME, extras)
        }
        val doneIntent = Intent(context, KwiatonomousBroadcastReceiver::class.java).apply {
            putExtra(EXTRA_PUMP_CLEANING_NAME, extras.copy(notificationIntentType = NotificationIntentType.DONE))
        }

        val postponePendingIntent = PendingIntent.getBroadcast(context, 0, postponeIntent, PendingIntent.FLAG_IMMUTABLE)
        val donePendingIntent = PendingIntent.getBroadcast(context, 1, doneIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, PUMP_CLEANING_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_v1)
            .setContentTitle(context.getString(R.string.pump_cleaning_reminder_notification_title))
            .setContentText(context.getString(R.string.pump_cleaning_reminder_notification_content).format(deviceName))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDeleteIntent(postponePendingIntent)
            .addAction(R.drawable.note, context.getString(R.string.postpone), postponePendingIntent)
            .addAction(R.drawable.app_icon_v1, context.getString(R.string.done), donePendingIntent)
            .build()

        if (isPermissionGranted(context)) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}