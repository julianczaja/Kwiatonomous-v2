package com.corrot.kwiatonomousapp.presentation.device_settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceConfigurationUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceNextWateringUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateDeviceConfigurationUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateDeviceNextWateringUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DeviceSettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getDeviceConfigurationUseCase: GetDeviceConfigurationUseCase,
    private val updateDeviceConfigurationUseCase: UpdateDeviceConfigurationUseCase,
    private val getDeviceNextWateringUseCase: GetDeviceNextWateringUseCase,
    private val updateDeviceNextWateringUseCase: UpdateDeviceNextWateringUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DeviceSettingsState())
    val state: State<DeviceSettingsState> = _state

    private var originalDeviceConfiguration: DeviceConfiguration? = null
    private var originalNextWatering: LocalDateTime? = null

    val settingsChanged: Boolean
        get() = (_state.value.deviceConfiguration != originalDeviceConfiguration || _state.value.nextWatering != originalNextWatering)

    init {
        savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
            getDeviceConfiguration(it)
            getDeviceNextWatering(it)
        }
    }

    fun refreshData() {
        savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
            getDeviceConfiguration(it)
            getDeviceNextWatering(it)
        }
    }

    fun saveNewDeviceConfiguration() {
        savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)?.let {
            updateDeviceConfiguration(it, _state.value.deviceConfiguration!!)

            if (_state.value.nextWatering != originalNextWatering) {
                updateNextWatering(it, _state.value.nextWatering!!)
            }
        }
        // TODO: show snackbar with success/failed
    }

    fun onDeviceConfigurationChanged(deviceConfiguration: DeviceConfiguration) {
        _state.value = _state.value.copy(deviceConfiguration = deviceConfiguration)
    }

    fun onDeviceWateringTimeChanged(hour: Int, minute: Int) {
        val newWateringDateTime =
            _state.value.nextWatering!!.withHour(hour).withMinute(minute).withSecond(0)

        _state.value = _state.value.copy(nextWatering = newWateringDateTime)
    }

    fun onDeviceWateringDateChanged(year: Int, month: Int, dayOfMonth: Int) {
        val newWateringDateTime =
            _state.value.nextWatering!!.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth)

        _state.value = _state.value.copy(nextWatering = newWateringDateTime)
    }

    private fun getDeviceConfiguration(id: String) {
        viewModelScope.launch {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> {
                        _state.value =
                            _state.value.copy(
                                deviceConfiguration = ret.data,
                                isLoading = false,
                                error = null
                            )
                        originalDeviceConfiguration = ret.data
                    }
                    is Result.Error -> _state.value =
                        DeviceSettingsState(error = ret.throwable.message)
                }
            }
        }
    }

    private fun getDeviceNextWatering(id: String) {
        viewModelScope.launch {
            getDeviceNextWateringUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> {
                        _state.value =
                            _state.value.copy(
                                nextWatering = ret.data,
                                isLoading = false,
                                error = null
                            )
                        originalNextWatering = ret.data
                    }
                    is Result.Error -> _state.value =
                        DeviceSettingsState(error = ret.throwable.message)
                }
            }
        }
    }

    private fun updateDeviceConfiguration(id: String, deviceConfiguration: DeviceConfiguration) {
        viewModelScope.launch {
            updateDeviceConfigurationUseCase.execute(id, deviceConfiguration)
        }
    }

    private fun updateNextWatering(id: String, newWateringTime: LocalDateTime) {
        viewModelScope.launch {
            updateDeviceNextWateringUseCase.execute(id, newWateringTime)
        }
    }
}