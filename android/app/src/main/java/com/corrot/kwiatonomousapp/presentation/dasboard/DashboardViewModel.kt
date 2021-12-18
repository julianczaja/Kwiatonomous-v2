package com.corrot.kwiatonomousapp.presentation.dasboard

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.components.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.LineChartDateType
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceConfigurationUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUpdatesByDateUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceUpdatesByDateUseCase: GetDeviceUpdatesByDateUseCase,
    private val getDeviceConfigurationUseCase: GetDeviceConfigurationUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DashboardState())
    val state: State<DashboardState> = _state

    private val _selectedChartDateTypeState = mutableStateOf(LineChartDateType.DAY)
    var selectedChartDateTypeState = _selectedChartDateTypeState

    private val _selectedChartDataTypeState = mutableStateOf(LineChartDataType.TEMPERATURE)
    var selectedChartDataTypeState = _selectedChartDataTypeState

    private val _currentDateRangeState = mutableStateOf(Pair(0L, 0L))
    val currentDateRangeState = _currentDateRangeState

    fun onChartDateTypeSelected(dateType: LineChartDateType) {
        val from: Long
        val to: Long
        val midnightTomorrow = LocalDateTime.now()
            .withHour(0)
            .withMinute(0)
            .plusDays(1L)
            .toLong()

        when (dateType) {
            LineChartDateType.DAY -> {
                from = midnightTomorrow - 24 * 3600
                to = midnightTomorrow
            }
            LineChartDateType.WEEK -> {
                from = midnightTomorrow - 7 * 24 * 3600
                to = midnightTomorrow
            }
            LineChartDateType.MONTH -> {
                from = midnightTomorrow - 28 * 24 * 3600
                to = midnightTomorrow
            }
        }
        _currentDateRangeState.value = Pair(from, to)

        savedStateHandle.get<String>(NAV_ARG_DEVICE_ID)?.let {
            getDeviceUpdates(
                it,
                _currentDateRangeState.value.first,
                _currentDateRangeState.value.second
            )
        }
        _selectedChartDateTypeState.value = dateType
    }

    fun onChartDataTypeSelected(dataType: LineChartDataType) {
        _selectedChartDataTypeState.value = dataType
    }

    init {
        onChartDateTypeSelected(LineChartDateType.DAY)
        savedStateHandle.get<String>(NAV_ARG_DEVICE_ID)?.let {
            getDevice(it)
            getDeviceUpdates(
                it,
                _currentDateRangeState.value.first,
                _currentDateRangeState.value.second
            )
            getDeviceConfiguration(it)
        }
    }

    fun refreshDevice() {
        savedStateHandle.get<String>(NAV_ARG_DEVICE_ID)?.let {

            getDevice(it)
            getDeviceUpdates(
                it,
                _currentDateRangeState.value.first,
                _currentDateRangeState.value.second
            )
            getDeviceConfiguration(it)
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

    private fun getDeviceUpdates(id: String, from: Long, to: Long) {
        viewModelScope.launch {
            getDeviceUpdatesByDateUseCase.execute(id, from, to)
                .collect { ret ->
                    Log.i("DashboardViewModel", "getDeviceUpdates: $ret")
                    when (ret) {
                        is Result.Loading -> _state.value =
                            _state.value.copy(isLoading = true, error = null)
                        is Result.Success -> _state.value =
                            _state.value.copy(
                                deviceUpdates = ret.data,
                                isLoading = false,
                                error = null
                            )
                        is Result.Error -> _state.value =
                            DashboardState(error = ret.throwable.message)
                    }
                }
        }
    }

    private fun getDeviceConfiguration(id: String) {
        viewModelScope.launch {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> _state.value =
                        _state.value.copy(
                            deviceConfiguration = ret.data,
                            isLoading = false,
                            error = null
                        )
                    is Result.Error -> _state.value =
                        DashboardState(error = ret.throwable.message)
                }
            }
        }
    }
}
