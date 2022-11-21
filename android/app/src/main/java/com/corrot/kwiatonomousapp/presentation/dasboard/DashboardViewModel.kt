package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.DeleteDeviceEventUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetAllDeviceEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userRepository: UserRepository,
    val authManager: AuthManager,
    private val getAllDeviceEventsUseCase: GetAllDeviceEventsUseCase,
    private val deleteDeviceEventUseCase: DeleteDeviceEventUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private companion object {
        const val EVENTS_LIMIT_PER_DEVICE = 3
        const val USER_KEY = "user"
    }

    private var selectedDeviceEvent: DeviceEvent? = null
    private val userFlow = savedStateHandle.getStateFlow<User?>(USER_KEY, null)
    val eventFlow = MutableSharedFlow<Event>()

    val uiState: StateFlow<DashboardState> = uiStateStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = DashboardState()
        )

    enum class Event {
        LOGGED_OUT
    }

    init {
        runBlocking {
            savedStateHandle[USER_KEY] = userRepository.getCurrentUserFromDatabase().firstOrNull()
        }
    }

    private fun uiStateStream(): Flow<DashboardState> = userFlow.flatMapLatest { user ->
        when (user) {
            null -> {
                logOut()
                return@flatMapLatest flowOf(DashboardState(error = "Can't find current user in database. Try logging again"))
            }
            else -> {
                if (user.devices.isEmpty()) {
                    return@flatMapLatest flowOf(uiState.value.copy(user = user))
                }

                val allDevicesEvents = mutableMapOf<String, List<DeviceEvent>>()
                val deviceEventFlows = user.devices.map {
                    getAllDeviceEventsUseCase.execute(
                        deviceId = it.deviceId,
                        limit = EVENTS_LIMIT_PER_DEVICE
                    )
                }
                // FIXME: When fetching 2 devices' events, the `Result.Success` is emmited 3 times for some reason
                deviceEventFlows
                    .merge()
                    .mapLatest { events ->
                        when (events) {
                            is Result.Loading -> {
                                return@mapLatest uiState.value.copy(
                                    user = user,
                                    isLoading = true,
                                    error = null
                                )
                            }
                            is Result.Success -> {
                                if (events.data.isNotEmpty()) {
                                    allDevicesEvents[events.data.first().deviceId] = events.data
                                }
                                return@mapLatest uiState.value.copy(
                                    user = user,
                                    isLoading = false,
                                    events = allDevicesEvents.flatMap { it.value }.sortedByDescending { it.timestamp }
                                )
                            }
                            is Result.Error -> {
                                return@mapLatest uiState.value.copy(
                                    user = user,
                                    isLoading = false,
                                    error = events.throwable.message ?: "Unknown error"
                                )
                            }
                        }
                    }
            }
        }
            .distinctUntilChanged()
    }


    fun refreshDevicesEvents() = viewModelScope.launch {
        userFlow.value?.let { user ->
            user.devices.forEach { userDevice ->
                getAllDeviceEventsUseCase.execute(
                    deviceId = userDevice.deviceId,
                    limit = EVENTS_LIMIT_PER_DEVICE
                ).collect()
            }
        }
    }

    fun logOut() = viewModelScope.launch {
        authManager.logOut()
        eventFlow.emit(Event.LOGGED_OUT)
    }

    fun getDeviceNameFromDeviceEvent(deviceEvent: DeviceEvent): String? {
        var deviceName: String? = null

        userFlow.value?.let { user ->
            val foundUserDevice = user.devices.find { it.deviceId == deviceEvent.deviceId }
            deviceName = foundUserDevice?.deviceName
        }

        return deviceName
    }

    fun selectEventToDelete(deviceEvent: DeviceEvent) {
        selectedDeviceEvent = deviceEvent
    }

    fun deleteSelectedUserEvent() = viewModelScope.launch(ioDispatcher) {
        selectedDeviceEvent?.let {
            deleteDeviceEventUseCase.execute(it).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) { // FIXME: Connect loading/error to uiState somehow
                        is Result.Loading -> Timber.e("Loading")
                        is Result.Success -> Timber.e("Success")
                        is Result.Error -> Timber.e("Error: ${ret.throwable}")
                    }
                    selectedDeviceEvent = null
                }
            }
        }
    }
}
