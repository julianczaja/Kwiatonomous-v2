package com.corrot.kwiatonomousapp.presentation.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.data.remote.api.KwiatonomousApi
import com.corrot.kwiatonomousapp.domain.model.RegisterCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    val kwiatonomousApi: KwiatonomousApi
) : ViewModel() {

    val state = mutableStateOf(RegisterScreenState())
    val eventFlow = MutableSharedFlow<Event>()

    enum class Event {
        REGISTERED
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
                // FIXME: Sending credentials in plain text using HTTP is stupid idea
                val response = kwiatonomousApi.registerNewAccount(
                    RegisterCredentials(
                        state.value.login, state.value.password
                    )
                )
                if (response.code() == 200) {
                    onRegistered()
                } else {
                    withContext(Dispatchers.Main) {
                        state.value = state.value.copy(
                            error = response.errorBody()?.string() ?: "Unknown error",
                            isLoading = false
                        )
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