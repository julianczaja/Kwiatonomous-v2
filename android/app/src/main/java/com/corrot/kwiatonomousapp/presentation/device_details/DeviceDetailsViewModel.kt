package com.corrot.kwiatonomousapp.presentation.device_details

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.components.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.LineChartDateType
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceConfigurationUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUpdatesByDateUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceUpdatesByDateUseCase: GetDeviceUpdatesByDateUseCase,
    private val getDeviceConfigurationUseCase: GetDeviceConfigurationUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DeviceDetailsState())
    val state: State<DeviceDetailsState> = _state

    init {
        onChartDateTypeSelected(LineChartDateType.WEEK)
        savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
            getDevice(it)
            getDeviceUpdates(
                id = it,
                from = _state.value.selectedDateRange.first,
                to = _state.value.selectedDateRange.second
            )
            getDeviceConfiguration(it)
        }
    }

    fun refreshDevice() {
        savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
            getDevice(it)
            getDeviceUpdates(
                id = it,
                from = _state.value.selectedDateRange.first,
                to = _state.value.selectedDateRange.second
            )
            getDeviceConfiguration(it)
        }
    }

    private fun getDevice(id: String) {
        viewModelScope.launch {
            getDeviceUseCase.execute(id).collect { ret ->
                Log.i("DeviceDetailsViewModel", "getDevice: $ret")
                _state.value = _state.value.copy(device = ret)
            }
        }
    }

    private fun getDeviceUpdates(id: String, from: Long, to: Long) {
        viewModelScope.launch {
            getDeviceUpdatesByDateUseCase.execute(id, from, to)
                .collect { ret ->
                    Log.i("DeviceDetailsViewModel", "getDeviceUpdates: $ret")
                    _state.value = _state.value.copy(deviceUpdates = ret)
                }
        }
    }

    private fun getDeviceConfiguration(id: String) {
        viewModelScope.launch {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                Log.i("DeviceDetailsViewModel", "getDeviceConfiguration: $ret")
                _state.value = _state.value.copy(deviceConfiguration = ret)
            }
        }
    }

    fun onChartDateTypeSelected(dateType: LineChartDateType) {
        when {
            dateType.ordinal < state.value.selectedChartDateType.ordinal -> {
                if (_state.value.deviceUpdates is Result.Success) {
                    val newDataRange = calculateDateRange(dateType)
                    val oldDeviceUpdates =
                        (_state.value.deviceUpdates as Result.Success<List<DeviceUpdate>>).data
                    val newDeviceUpdates = oldDeviceUpdates.filter {
                        it.updateTime.toLong() in newDataRange.first..newDataRange.second
                    }

                    _state.value = _state.value.copy(
                        deviceUpdates = Result.Success(newDeviceUpdates),
                        selectedChartDateType = dateType,
                        selectedDateRange = newDataRange
                    )
                }
            }
            dateType.ordinal == state.value.selectedChartDateType.ordinal -> {
                return
            }
            dateType.ordinal > state.value.selectedChartDateType.ordinal -> {
                val newDataRange = calculateDateRange(dateType)
                _state.value = _state.value.copy(
                    selectedChartDateType = dateType,
                    selectedDateRange = newDataRange
                )
                savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
                    getDeviceUpdates(
                        id = it,
                        from = newDataRange.first,
                        to = newDataRange.second
                    )
                }
            }
        }
    }

    fun onChartDataTypeSelected(dataType: LineChartDataType) {
        _state.value = _state.value.copy(selectedChartDataType = dataType)
    }

    private fun calculateDateRange(dateType: LineChartDateType): Pair<Long, Long> {
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
        return Pair(from, to)
    }
}
