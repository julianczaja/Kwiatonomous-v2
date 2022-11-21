package com.corrot.kwiatonomousapp.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDevicesWithLastUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getUserDevicesWithLastUpdatesUseCase: GetUserDevicesWithLastUpdatesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(DevicesState())
    val state: StateFlow<DevicesState> = _state

    init {
        refreshData()
    }

    fun refreshData() = viewModelScope.launch(ioDispatcher) {
        getUserDevicesWithLastUpdatesUseCase.execute()
            .collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isLoading = true, userDevicesWithLastUpdates = ret.data) }
                    is Result.Success -> _state.update { it.copy(isLoading = false, userDevicesWithLastUpdates = ret.data) }
                    is Result.Error -> _state.update { it.copy(isLoading = false, error = ret.throwable.message ?: "Unknown error") }
                }
            }
    }

    fun confirmError() = _state.update { it.copy(error = null) }
}
