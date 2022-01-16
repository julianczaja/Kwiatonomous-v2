package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase
) : ViewModel() {

    val state = mutableStateOf(DevicesState())
    private var getDevicesJob: Job? = null

    init {
        getDevices()
    }

    fun refreshDevices() {
        getDevices()
    }

    fun confirmError() {
        state.value = state.value.copy(error = null)
    }

    private fun getDevices() {
        viewModelScope.launch {
            getDevicesJob?.cancelAndJoin()
            getDevicesJob = viewModelScope.launch(Dispatchers.IO) {
                getDevicesUseCase.execute().collect { ret ->
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> state.value = state.value.copy(
                                isLoading = true, devices = ret.data, error = null
                            )
                            is Result.Success -> state.value = state.value.copy(
                                isLoading = false, devices = ret.data, error = null
                            )
                            is Result.Error -> state.value = state.value.copy(
                                isLoading = false, error = ret.throwable.message
                            )
                        }
                    }
                }
            }
        }
    }
}