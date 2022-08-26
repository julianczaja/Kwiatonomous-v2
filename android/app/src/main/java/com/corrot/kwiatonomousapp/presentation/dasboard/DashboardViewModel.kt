package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.AuthManager
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.DeleteDeviceEventUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetAllDeviceEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val userRepository: UserRepository,
    val authManager: AuthManager,
    private val getAllDeviceEventsUseCase: GetAllDeviceEventsUseCase,
    private val deleteDeviceEventUseCase: DeleteDeviceEventUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val state = mutableStateOf(DashboardState())
    val eventFlow = MutableSharedFlow<Event>()

    private var getEventsJob: Job? = null
    private lateinit var userDevices: List<UserDevice>
    private var selectedDeviceEvent: DeviceEvent? = null

    enum class Event {
        LOGGED_OUT
    }

    init {
        val user = runBlocking { userRepository.getCurrentUserFromDatabase().firstOrNull() }
        if (user != null) {
            state.value = state.value.copy(
                user = user,
                isLoading = false,
                error = null
            )
            userDevices = user.devices
            getDevicesEvents(userDevices)
        } else {
            state.value = state.value.copy(
                user = user,
                isLoading = false,
                error = "Can't find current user in database. Try logging again"
            )
            logOut()
        }
    }

    fun refreshDevicesEvents() {
        getDevicesEvents(userDevices)
    }

    private fun getDevicesEvents(userDevices: List<UserDevice>) = viewModelScope.launch {
        val allEvents = mutableListOf<DeviceEvent>()
        state.value = state.value.copy(
            isLoading = true,
            events = allEvents
        )
        getEventsJob?.cancelAndJoin()
        getEventsJob = viewModelScope.launch(Dispatchers.IO) {
            userDevices.map { getAllDeviceEventsUseCase.execute(it.deviceId, 50) }
                .merge()
                .collectLatest { ret ->
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> state.value = state.value.copy(
                                isLoading = true,
                                error = null
                            )
                            is Result.Success -> {
                                ret.data.forEach { deviceEvent ->
                                    // FIXME: that's not an optimal way to do this
                                    // FIXME: It won't change list when something is removed!!!
                                    if (allEvents.find { it.timestamp == deviceEvent.timestamp } == null) {
                                        allEvents.add(deviceEvent)
                                    }
                                }
                                state.value = state.value.copy(
                                    isLoading = false,
                                    events = allEvents.sortedByDescending { it.timestamp }
                                )
                            }
                            is Result.Error -> state.value = state.value.copy(
                                isLoading = false,
                                error = ret.throwable.message ?: "Unknown error"
                            )
                        }
                    }
                }
        }
    }

    fun logOut() = viewModelScope.launch {
        authManager.logOut()
        eventFlow.emit(Event.LOGGED_OUT)
    }

    fun selectEventToDelete(deviceEvent: DeviceEvent) {
        selectedDeviceEvent = deviceEvent
    }

    fun deleteSelectedUserEvent() = viewModelScope.launch(ioDispatcher) {
        if (selectedDeviceEvent != null) {
            deleteDeviceEventUseCase.execute(selectedDeviceEvent!!).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                    selectedDeviceEvent = null
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    error = "There is no selected event" // FIXME
                )
            }
        }
    }
}
