package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.AuthManager
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val userRepository: UserRepository,
    val authManager: AuthManager
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

    fun logOut() = viewModelScope.launch {
        authManager.logOut()
        eventFlow.emit(Event.LOGGED_OUT)
    }
}
