package com.corrot.kwiatonomousapp.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants.REGEX_ALPHANUMERIC_WITHOUT_SPACE
import com.corrot.kwiatonomousapp.common.Constants.REGEX_ALPHANUMERIC_WITH_SPACE
import com.corrot.kwiatonomousapp.domain.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    val authManager: AuthManager,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state: StateFlow<RegisterScreenState> = _state

    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        REGISTERED
    }

    fun userNameChanged(newUserName: String) {
        if (newUserName.matches(REGEX_ALPHANUMERIC_WITH_SPACE)) {
            _state.update { it.copy(userName = newUserName) }
        }
    }

    fun loginChanged(newLogin: String) {
        if (newLogin.matches(REGEX_ALPHANUMERIC_WITHOUT_SPACE)) {
            _state.update { it.copy(login = newLogin) }
        }
    }

    fun passwordChanged(newPassword: String) {
        if (newPassword.matches(REGEX_ALPHANUMERIC_WITHOUT_SPACE)) {
            _state.update { it.copy(password = newPassword) }
        }
    }

    fun registerClicked() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                authManager.tryToRegister(_state.value.userName, _state.value.login, _state.value.password)
                onRegistered()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
                }
            }
        }
    }

    private fun onRegistered() {
        _state.update { it.copy(error = null, isLoading = false) }
        viewModelScope.launch { eventFlow.emit(Event.REGISTERED) }
    }

    fun confirmError() = _state.update { it.copy(error = null) }
}