package com.corrot.kwiatonomousapp.presentation.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    val authManager: AuthManager,
) : ViewModel() {

    val state = mutableStateOf(RegisterScreenState())
    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        REGISTERED
    }

    fun userNameChanged(newUserName: String) {
        // TODO: add validation
        state.value = state.value.copy(userName = newUserName)
    }

    fun loginChanged(newLogin: String) {
        // TODO: add validation
        state.value = state.value.copy(login = newLogin)
    }

    fun passwordChanged(newPassword: String) {
        // TODO: add validation
        state.value = state.value.copy(password = newPassword)
    }

    fun registerClicked() {
        state.value = state.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                authManager.tryToRegister(
                    state.value.userName,
                    state.value.login,
                    state.value.password
                )
                onRegistered()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    state.value = state.value.copy(
                        error = e.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun confirmError() {
        state.value = state.value.copy(error = null)
    }

    private fun onRegistered() {
        state.value = state.value.copy(
            error = null,
            isLoading = false
        )
        viewModelScope.launch {
            eventFlow.emit(Event.REGISTERED)
        }
    }
}