package com.corrot.kwiatonomousapp.domain.workmanager

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.domain.model.toDeviceUpdateEntity
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.presentation.widget.DevicesWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

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
        try {
            if (!authManager.checkIfLoggedIn()) {
                Timber.e("User not logged in")
                throw Exception("User not logged in")
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            return if (runAttemptCount < 10) {
                Result.retry()
            } else {
                Result.failure()
            }
        }

        userRepository.getCurrentUserFromDatabase().first()?.let { user ->
            return try {
                user.devices.forEach { userDevice ->
                    deviceUpdateRepository
                        .fetchAllDeviceUpdates(userDevice.deviceId, 1)
                        .map { it.toDeviceUpdate().toDeviceUpdateEntity() }
                        .let { deviceUpdateRepository.saveFetchedDeviceUpdates(it) }
                }
                DevicesWidget().updateAll(applicationContext)
                Result.success()
            } catch (e: Exception) {
                Timber.e("Error: ${e.message}")
                if (runAttemptCount < 10) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
        return Result.success()
    }
}
