package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.AuthManager
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.GetAllDeviceEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val userRepository: UserRepository,
    val authManager: AuthManager,
    private val getAllDeviceEventsUseCase: GetAllDeviceEventsUseCase,
) : ViewModel() {

    val state = mutableStateOf(DashboardState())
    val eventFlow = MutableSharedFlow<Event>()

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
            getDeviceEvents(user.devices)
        } else {
            state.value = state.value.copy(
                user = user,
                isLoading = false,
                error = "Can't find current user in database. Try logging again"
            )
            logOut()
        }
    }

    private fun getDeviceEvents(userDevices: List<UserDevice>) {
        val allEvents = mutableListOf<DeviceEvent>()
        state.value = state.value.copy(
            isLoading = true,
            events = allEvents
        )

        viewModelScope.launch(Dispatchers.IO) {
            userDevices.map { getAllDeviceEventsUseCase.execute(it.deviceId, 50) }
                .merge()
                .collectLatest { ret ->
                    Timber.e(ret.toString())
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> state.value = state.value.copy(
                                isLoading = true,
                                error = null
                            )
                            is Result.Success -> {
                                allEvents.addAll(ret.data)
                                state.value = state.value.copy(
                                    isLoading = false,
                                    events = allEvents.sortedBy { it.timestamp }
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
}
