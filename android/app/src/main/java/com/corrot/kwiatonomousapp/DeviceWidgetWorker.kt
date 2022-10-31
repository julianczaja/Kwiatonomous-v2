package com.corrot.kwiatonomousapp

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.DevicesWidgetData
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.presentation.widget.DevicesWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDateTime

@HiltWorker
class DeviceWidgetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    private val deviceUpdateRepository: DeviceUpdateRepository,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val DEVICE_WIDGET_WORK_TAG = "DEVICE_WIDGET_WORK_TAG"
        const val DEVICE_WIDGET_WORK_NAME = "DEVICE_WIDGET_WORK_NAME"
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(applicationContext)
        val glanceIds = manager.getGlanceIds(DevicesWidget::class.java)

        setWidgetState(glanceIds, DevicesWidgetData.Loading)

        try {
            if (!authManager.checkIfLoggedIn()) {
                Timber.e("User not logged in")
                throw Exception("User not logged in")
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            setWidgetState(glanceIds, DevicesWidgetData.Error(e.message ?: "Unknown error"))
            return if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }

        userRepository.getCurrentUserFromDatabase().first()?.let { user ->

            return try {
                val devicesUpdates = mutableMapOf<UserDevice, DeviceUpdate>()
                user.devices.forEach { userDevice ->
                    deviceUpdateRepository
                        .fetchAllDeviceUpdates(userDevice.deviceId, 1)
                        .first()
                        .toDeviceUpdate()
                        .also { devicesUpdates[userDevice] = it }
                }
                setWidgetState(glanceIds, DevicesWidgetData.Success(LocalDateTime.now(), devicesUpdates))

                Result.success()
            } catch (e: Exception) {
                setWidgetState(glanceIds, DevicesWidgetData.Error(e.message.orEmpty()))
                if (runAttemptCount < 10) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }

        return Result.success()
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: DevicesWidgetData) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = applicationContext,
                definition = DevicesWidgetData.StateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        DevicesWidget().updateAll(applicationContext)
    }
}
