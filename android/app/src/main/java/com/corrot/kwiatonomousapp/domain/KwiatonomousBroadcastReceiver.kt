package com.corrot.kwiatonomousapp.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.parcelable
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.NotificationIntentType
import com.corrot.kwiatonomousapp.domain.model.PumpCleaningNotificationExtras
import com.corrot.kwiatonomousapp.domain.usecase.AddDeviceEventUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateDeviceLastPumpCleaningUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class KwiatonomousBroadcastReceiver : BroadcastReceiver() {

    private companion object {
        const val DEFAULT_NOTIFICATION_ID = -12345
    }

    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var addDeviceEvent: AddDeviceEventUseCase

    @Inject
    lateinit var updateDeviceLastPumpCleaning: UpdateDeviceLastPumpCleaningUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult: PendingResult = goAsync()

        scope.launch(Dispatchers.Default) {
            try {
                Timber.e(intent.toUri(0))

                val extras = intent.parcelable<PumpCleaningNotificationExtras>(NotificationsManager.EXTRA_PUMP_CLEANING_NAME)
                    ?: throw Exception("Cannot find PumpCleaningNotificationExtras")

                Timber.d("extras = $extras")

                val shouldAddEvent = !extras.isTest
                        && extras.notificationIntentType == NotificationIntentType.DONE
                        && extras.notificationId != DEFAULT_NOTIFICATION_ID
                        && extras.deviceId.isNotEmpty()

                if (shouldAddEvent) {
                    updateDeviceLastPumpCleaning.execute(extras.deviceId, LocalDateTime.now())
                        .onEach { if (it is Result.Error) throw it.throwable }
                        .collect()
                    addDeviceEvent.execute(DeviceEvent.PumpCleaning(extras.deviceId, LocalDateTime.now()))
                        .onEach { if (it is Result.Error) throw it.throwable }
                        .collect()
                }

                NotificationManagerCompat.from(context).cancel(extras.notificationId)
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
