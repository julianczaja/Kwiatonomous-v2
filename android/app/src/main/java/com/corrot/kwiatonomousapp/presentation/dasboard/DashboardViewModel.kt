package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUpdatesUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceUpdatesUseCase: GetDeviceUpdatesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DashboardState())
    val state: State<DashboardState> = _state


//    private val _deviceState = mutableStateOf(DashboardState())
//    val deviceState: State<DashboardState> = _deviceState
//
//    private val _deviceUpdateState = mutableStateOf(DashboardState())
//    val deviceUpdateState: State<DashboardState> = _deviceUpdateState

    init {
        savedStateHandle.get<String>(NAV_ARG_DEVICE_ID)?.let {
            getDevice(it)
            getDeviceUpdates(it)
        }
    }

    fun refreshDevice() {
        savedStateHandle.get<String>(NAV_ARG_DEVICE_ID)?.let {
            getDevice(it)
            getDeviceUpdates(it)
        }
    }

    private fun getDevice(id: String) {
        viewModelScope.launch {
            getDeviceUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> _state.value =
                        _state.value.copy(device = ret.data, isLoading = false, error = null)
                    is Result.Error -> _state.value =
                        DashboardState(error = ret.throwable.message)
                }
            }
        }
    }

    private fun getDeviceUpdates(id: String) {
        viewModelScope.launch {
            getDeviceUpdatesUseCase.execute(id, 100).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> _state.value =
                        _state.value.copy(deviceUpdates = ret.data, isLoading = false, error = null)
                    is Result.Error -> _state.value =
                        DashboardState(error = ret.throwable.message)
                }
            }
        }
    }
}
