package com.corrot.kwiatonomousapp.presentation.dasboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDeviceUseCase: GetDeviceUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DashboardState())
    val state: State<DashboardState> = _state

    init {
        getDevice("MnOEsFhVNCCG")
    }

    private fun getDevice(id: String) {
        viewModelScope.launch {
            getDeviceUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value = DashboardState(isLoading = true)
                    is Result.Success -> _state.value = DashboardState(device = ret.data)
                    is Result.Error -> _state.value = DashboardState(error = ret.throwable.message)
                }
            }
        }
    }
}
