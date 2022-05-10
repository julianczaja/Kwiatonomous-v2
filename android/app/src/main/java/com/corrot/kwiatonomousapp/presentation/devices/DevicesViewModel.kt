package com.corrot.kwiatonomousapp.presentation.devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDevicesWithLastUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getUserDevicesWithLastUpdatesUseCase: GetUserDevicesWithLastUpdatesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val state = mutableStateOf(DevicesState())

    init {
        refreshData()
    }

    fun confirmError() {
        state.value = state.value.copy(error = null)
    }

    fun refreshData() {
        state.value = state.value.copy(isLoading = true)

        viewModelScope.launch(ioDispatcher) {
            getUserDevicesWithLastUpdatesUseCase
                .execute()
                .collect { userDevicesAndLastUpdates ->
                    withContext(Dispatchers.Main) {
                        state.value = state.value.copy(
                            isLoading = false,
                            userDevicesWithLastUpdates = userDevicesAndLastUpdates,
                            error = null
                        )
                    }
                }
        }
    }
}