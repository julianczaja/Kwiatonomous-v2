package com.corrot.kwiatonomousapp.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.LoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginManager: LoginManager
) : ViewModel() {

    val state = mutableStateOf(LoginScreenState())
    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        LOGGED_IN
    }

    fun loginChanged(newLogin: String) {
        // TODO: add validation
        state.value = state.value.copy(login = newLogin)
    }

    fun passwordChanged(newPassword: String) {
        // TODO: add validation
        state.value = state.value.copy(password = newPassword)
    }

    fun loginClicked() {
        state.value = state.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                if (loginManager.checkIfLoggedIn(state.value.login, state.value.password)) {
                    onLoggedIn()
                } else {
                    if (loginManager.tryToLogin(state.value.login, state.value.password)) {
                        onLoggedIn()
                    }
                }
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

    private fun onLoggedIn() {
        state.value = state.value.copy(
            error = null,
            isLoading = false
        )
        viewModelScope.launch {
            eventFlow.emit(Event.LOGGED_IN)
        }
    }

    // ------------------------------------------------------------------------------------------ //


}

data class AuthHeader(
    val realm: String,
    val nonce: String,
    val algorithm: String
)
