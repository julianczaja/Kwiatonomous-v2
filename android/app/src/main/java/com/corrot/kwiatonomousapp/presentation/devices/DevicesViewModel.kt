package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state = mutableStateOf(DevicesState())

    init {
        getDevices()
    }

    fun confirmError() {
        state.value = state.value.copy(error = null)
    }

    private fun getDevices() = viewModelScope.launch(ioDispatcher) {
        userRepository.getCurrentUserFromDatabase().collect { user ->
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    isLoading = false,
                    userDevices = user!!.devices,
                    error = null
                )
            }
        }
    }
}