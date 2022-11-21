package com.corrot.kwiatonomousapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.domain.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authManager: AuthManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state: StateFlow<LoginScreenState> = _state

    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        LOGGED_IN
    }

    fun loginChanged(newLogin: String) {
        if (newLogin.matches(Constants.REGEX_ALPHANUMERIC_WITHOUT_SPACE)) {
            _state.update { it.copy(login = newLogin) }
        }
    }

    fun passwordChanged(newPassword: String) {
        if (newPassword.matches(Constants.REGEX_ALPHANUMERIC_WITHOUT_SPACE)) {
            _state.update { it.copy(password = newPassword) }
        }
    }

    fun loginClicked() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                authManager.tryToLogin(state.value.login, state.value.password)
                onLoggedIn()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }

    private fun onLoggedIn() {
        _state.update { it.copy(error = null, isLoading = false) }
        viewModelScope.launch {
            eventFlow.emit(Event.LOGGED_IN)
        }
    }

    fun confirmError() = _state.update { it.copy(error = null) }
}
