package com.corrot.kwiatonomousapp.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDevicesWithLastUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getUserDevicesWithLastUpdatesUseCase: GetUserDevicesWithLastUpdatesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(DevicesState())
    val state: StateFlow<DevicesState> = _state

    private var getDataJob: Job? = null

    init {
        refreshData()
    }

    fun refreshData() = viewModelScope.launch(ioDispatcher) {
        getDataJob?.cancelAndJoin()
        getDataJob = viewModelScope.launch(ioDispatcher) {
            getUserDevicesWithLastUpdatesUseCase.execute().cancellable().collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update {
                        it.copy(isLoading = true, userDevicesWithLastUpdates = filterNewUpdates(ret.data))
                    }
                    is Result.Success -> _state.update {
                        it.copy(isLoading = false, userDevicesWithLastUpdates = ret.data)
                    }
                    is Result.Error -> _state.update {
                        it.copy(isLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun confirmError() = _state.update { it.copy(error = null) }

    private fun filterNewUpdates(data: List<Pair<UserDevice, DeviceUpdate?>>?) = data?.map {
        // Preload only relatively new updates
        val hoursSinceLastUpdate = it.second?.updateTime?.let { updateTime ->
            ChronoUnit.HOURS.between(updateTime, LocalDateTime.now())
        }

        if (hoursSinceLastUpdate != null && hoursSinceLastUpdate <= 1) {
            return@map Pair(it.first,it.second)
        } else {
            return@map Pair(it.first, null)
        }
    }
}
