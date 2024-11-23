package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.AuthManager
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.repository.DeviceEventRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.DeleteDeviceEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userRepository: UserRepository,
    val authManager: AuthManager,
    private val deviceEventRepository: DeviceEventRepository,
    private val deleteDeviceEventUseCase: DeleteDeviceEventUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private companion object {
        const val EVENTS_LIMIT_PER_DEVICE = 5
        const val USER_KEY = "user"
    }

    private var selectedDeviceEvent: DeviceEvent? = null
    private val userFlow = savedStateHandle.getStateFlow<User?>(USER_KEY, null)
    val eventFlow = MutableSharedFlow<Event>()

    private val isError = MutableStateFlow<String?>(null)
    private val isLoading = MutableStateFlow(false)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun devicesEventsStream(): Flow<List<DeviceEvent>> = userFlow.flatMapLatest { user ->
        if (user == null) {
            return@flatMapLatest emptyFlow()
        } else {
            val allDevicesEvents = mutableMapOf<String, List<DeviceEvent>>()
            return@flatMapLatest user.devices
                .map {
                    deviceEventRepository
                        .getAllDeviceEventsFromDatabase(it.deviceId, EVENTS_LIMIT_PER_DEVICE)
                        .distinctUntilChanged(::checkIfTwoDeviceEventsListsAreTheEquivalent)
                }
                .merge()
                .onStart { refreshDevicesEvents() }
                .mapLatest { events ->
                    if (events.isNotEmpty()) {
                        allDevicesEvents[events.first().deviceId] = events
                    }
                    return@mapLatest allDevicesEvents.flatMap { it.value }.sortedByDescending { it.timestamp }
                }
        }
    }

    private fun uiStateStream(): Flow<DashboardState> = combine(
        userFlow,
        devicesEventsStream(),
        isLoading,
        isError
    ) { user, devicesEvents, isLoading, error ->
        when (user) {
            null -> {
                logOut()
                DashboardState(error = "Can't find current user in database. Try logging again")
            }
            else -> DashboardState(
                user = user,
                isLoading = isLoading,
                events = devicesEvents,
                error = error
            )
        }
    }

    fun refreshDevicesEvents() = viewModelScope.launch(ioDispatcher) {
        userFlow.value?.let { user ->
            isLoading.emit(true)
            user.devices.forEach { userDevice ->
                try {
                    deviceEventRepository.updateAllDeviceEvents(userDevice.deviceId, EVENTS_LIMIT_PER_DEVICE)
                } catch (e: Exception) {
                    isError.emit(e.message ?: "Unknown error")
                } finally {
                    isLoading.emit(false)
                }
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
                when (ret) {
                    is Result.Loading -> isLoading.emit(true)
                    is Result.Success -> isLoading.emit(false)
                    is Result.Error -> {
                        isLoading.emit(false)
                        isError.emit(ret.throwable.message ?: "Unknown error")
                    }
                }
                selectedDeviceEvent = null
            }
        }
    }

    private fun checkIfTwoDeviceEventsListsAreTheEquivalent(old: List<DeviceEvent>, new: List<DeviceEvent>) =
        if (old.isNotEmpty() && new.isNotEmpty()) {
            old.map { it.timestamp } == new.map { it.timestamp }
        } else {
            old.size == new.size
        }
}
