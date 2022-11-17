package com.corrot.kwiatonomousapp.presentation.device_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class DeviceSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDeviceConfigurationUseCase: GetDeviceConfigurationUseCase,
    private val updateDeviceConfigurationUseCase: UpdateDeviceConfigurationUseCase,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val updateDeviceNextWateringUseCase: UpdateDeviceNextWateringUseCase,
    private val addDeviceEventUseCase: AddDeviceEventUseCase,
) : ViewModel() {

    private val deviceId = savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)

    private val _state = MutableStateFlow(DeviceSettingsState())
    val state: StateFlow<DeviceSettingsState> = _state

    private var _originalDeviceConfiguration: DeviceConfiguration? = null
    private var _originalNextWatering: LocalDateTime? = null

    val settingsChanged: Boolean
        get() = _state.value.deviceConfiguration != _originalDeviceConfiguration || _state.value.nextWatering != _originalNextWatering

    init {
        if (deviceId.isNullOrEmpty()) {
            _state.update { DeviceSettingsState(error = "Unknown error") }
        } else {
            getData(deviceId)
        }
    }

    private fun getData(deviceId: String) = viewModelScope.launch {
        getDeviceConfigurationUseCase.execute(deviceId)
            .combine(getDeviceUseCase.execute(deviceId)) { deviceConfiguration: Result<DeviceConfiguration>, device: Result<Device> ->

                _state.update { it.copy(isLoading = deviceConfiguration is Result.Loading || device is Result.Loading) }

                if (deviceConfiguration is Result.Error) {
                    _state.update { it.copy(error = deviceConfiguration.throwable.message ?: "Unknown error") }
                    return@combine
                }
                if (device is Result.Error) {
                    _state.update { it.copy(error = device.throwable.message ?: "Unknown error") }
                    return@combine
                }

                if (deviceConfiguration is Result.Success) {
                    _state.update { it.copy(deviceConfiguration = deviceConfiguration.data, error = null) }
                    _originalDeviceConfiguration = deviceConfiguration.data
                }
                if (device is Result.Success) {
                    _state.update { it.copy(nextWatering = device.data.nextWatering) }
                    _originalNextWatering = device.data.nextWatering
                }
            }
            .collect()
    }

    fun resetChanges() = _state.update {
        it.copy(
            deviceConfiguration = _originalDeviceConfiguration,
            nextWatering = _originalNextWatering,
            isLoading = false,
            error = null
        )
    }

    fun saveNewDeviceConfiguration() = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateDeviceConfiguration(deviceId!!, _state.value.deviceConfiguration!!)

            if (_state.value.nextWatering != _originalNextWatering) {
                updateNextWatering(deviceId, _state.value.nextWatering!!)
            }

            addNewDeviceEvent(DeviceEvent.ConfigurationChange(deviceId, LocalDateTime.now()))
        } catch (e: Exception) {
            Timber.e(e)
            _state.update { it.copy(error = "Error occurred while applying changes. ${e.message}") }
        }
    }

    fun onDeviceConfigurationChanged(deviceConfiguration: DeviceConfiguration) {
        _state.update { it.copy(deviceConfiguration = deviceConfiguration) }
    }

    fun onDeviceTimeZoneChanged(timeZoneString: String) {
        fun String.toTimeZone() = if (this == "UTC") ZoneOffset.UTC else ZoneOffset.of(this.replace("UTC", ""))

        onDeviceConfigurationChanged(_state.value.deviceConfiguration!!.copy(timeZoneOffset = timeZoneString.toTimeZone()))
    }

    fun onDeviceWateringTimeChanged(localTime: LocalTime) {
        // Convert new watering datetime to be in device time zone
        val currentZoneOffset = _state.value.deviceConfiguration?.timeZoneOffset
        if (currentZoneOffset != null) {
            val newWateringDateTime = _state.value.nextWatering!!
                .withHour(localTime.hour)
                .withMinute(localTime.minute)
                .withSecond(0)
                .atOffset(currentZoneOffset)
                .toLocalDateTime()

            _state.update { it.copy(nextWatering = newWateringDateTime) }
            onDeviceConfigurationChanged(_state.value.deviceConfiguration!!.copy(wateringTime = localTime))
        } else {
            // TODO: Handle exception
            Timber.tag("onDeviceWateringDateChanged")
                .e("Can't update watering time, because the current zone offset is unknown (null)")
        }
    }

    fun onDeviceWateringDateChanged(year: Int, month: Int, dayOfMonth: Int) {
        // Convert new watering datetime to be in device time zone
        val currentZoneOffset = _state.value.deviceConfiguration?.timeZoneOffset
        if (currentZoneOffset != null) {
            val newWateringDateTime = _state.value.nextWatering!!
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(dayOfMonth)
                .atOffset(currentZoneOffset)
                .toLocalDateTime()

            _state.update { it.copy(nextWatering = newWateringDateTime) }
        } else {
            // TODO: Handle exception
            Timber.tag("onDeviceWateringDateChanged")
                .e("Can't update watering time, because the current zone offset is unknown (null)")
        }
    }

    private suspend fun updateDeviceConfiguration(deviceId: String, deviceConfiguration: DeviceConfiguration) {
        updateDeviceConfigurationUseCase.execute(deviceId, deviceConfiguration).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                    _originalDeviceConfiguration = deviceConfiguration
                }
                is Result.Error -> _state.update { DeviceSettingsState(error = ret.throwable.message ?: "Unknown error") }
            }
        }
    }

    private suspend fun updateNextWatering(deviceId: String, newWateringTime: LocalDateTime) {
        updateDeviceNextWateringUseCase.execute(deviceId, newWateringTime).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, error = null) }
                    _originalNextWatering = newWateringTime
                }
                is Result.Error -> _state.update {
                    DeviceSettingsState(isLoading = false, error = ret.throwable.message ?: "Unknown error")
                }
            }
        }
    }

    private suspend fun addNewDeviceEvent(deviceEvent: DeviceEvent) {
        addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                is Result.Success -> _state.update { it.copy(isLoading = false, error = null) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = ret.throwable.message ?: "Unknown error") }
            }
        }
    }
}
