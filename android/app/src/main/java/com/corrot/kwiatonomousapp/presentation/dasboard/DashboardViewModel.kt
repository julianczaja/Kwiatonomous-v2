package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.AuthManager
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.GetAllDeviceEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            val user = userRepository.getCurrentUserFromDatabase().firstOrNull()
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
    }

    private suspend fun getDeviceEvents(userDevices: List<UserDevice>) {
        // FIXME - get all devices' events and sort by timestamp
        userDevices.firstOrNull()?.let { userDevice ->
            getAllDeviceEventsUseCase.execute(userDevice.deviceId, 100).collect { ret ->
                when (ret) {
                    is Result.Loading -> state.value = state.value.copy(
                        isLoading = true,
                        error = null
                    )
                    is Result.Success ->
                        state.value = state.value.copy(
                            events = ret.data.sortedBy { it.timestamp },
                            isLoading = false
                        )
                    is Result.Error -> state.value = state.value.copy(
                        isLoading = false,
                        error = ret.throwable.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun logOut() = viewModelScope.launch {
        authManager.logOut()
        eventFlow.emit(Event.LOGGED_OUT)
    }
}
