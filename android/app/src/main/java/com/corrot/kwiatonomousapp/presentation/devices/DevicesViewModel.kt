package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val userDeviceRepository: UserDeviceRepository,
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
        userDeviceRepository.getUserDevices().collect { userDevices ->
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    isLoading = false,
                    userDevices = userDevices,
                    error = null
                )
            }
        }
    }
}