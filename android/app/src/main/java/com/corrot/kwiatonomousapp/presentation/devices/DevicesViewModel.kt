package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DevicesState())
    val state: State<DevicesState> = _state

    init {
        getDevices()
    }

    fun refreshDevices() {
        getDevices()
    }

    private fun getDevices() {
        viewModelScope.launch {
            getDevicesUseCase.execute().collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value = DevicesState(isLoading = true)
                    is Result.Success -> _state.value = DevicesState(devices = ret.data)
                    is Result.Error -> _state.value = DevicesState(error = ret.throwable.message)
                }
            }
        }
    }
}