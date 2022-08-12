package com.corrot.kwiatonomousapp.presentation.device_settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
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

    private val _state = mutableStateOf(DeviceSettingsState())
    val state: State<DeviceSettingsState> = _state

    // mutableStateOf type to trigger isModified on value change
    private var _originalDeviceConfiguration = mutableStateOf<DeviceConfiguration?>(null)
    private var _originalNextWatering = mutableStateOf<LocalDateTime?>(null)

    val settingsChanged: Boolean
        get() = _state.value.deviceConfiguration != _originalDeviceConfiguration.value
                || _state.value.nextWatering != _originalNextWatering.value

    init {
        refreshData()
    }

    fun refreshData() {
        deviceId?.let {
            getDeviceConfiguration(it)
            getDeviceNextWatering(it)
        }
    }

    fun resetChanges() {
        _state.value = _state.value.copy(
            deviceConfiguration = _originalDeviceConfiguration.value,
            isLoading = false,
            error = null
        )
    }

    fun saveNewDeviceConfiguration() = viewModelScope.launch(Dispatchers.IO) {
        try {
            deviceId?.let {
                updateDeviceConfiguration(it, _state.value.deviceConfiguration!!)

                if (_state.value.nextWatering != _originalNextWatering.value) {
                    updateNextWatering(it, _state.value.nextWatering!!)
                }

                addNewDeviceEvent(DeviceEvent.ConfigurationChange(it, LocalDateTime.now()))
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private suspend fun addNewDeviceEvent(deviceEvent: DeviceEvent) {
        addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
            withContext(Dispatchers.Main) {
                when (ret) {
                    is Result.Loading -> _state.value = state.value.copy(
                        isLoading = true,
                        error = null
                    )
                    is Result.Success -> _state.value = state.value.copy(
                        isLoading = false
                    )
                    is Result.Error -> _state.value = state.value.copy(
                        isLoading = false,
                        error = ret.throwable.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun onDeviceConfigurationChanged(deviceConfiguration: DeviceConfiguration) {
        _state.value = _state.value.copy(deviceConfiguration = deviceConfiguration)
    }

    fun onDeviceTimeZoneChanged(timeZoneString: String) {
        val zoneOffset = if (timeZoneString == "UTC") {
            ZoneOffset.UTC
        } else {
            ZoneOffset.of(timeZoneString.replace("UTC", ""))
        }

        // TODO: What should happen when time zone has changed?

        onDeviceConfigurationChanged(
            _state.value.deviceConfiguration!!.copy(timeZoneOffset = zoneOffset)
        )
    }

    fun onDeviceWateringTimeChanged(hour: Int, minute: Int) {
        // Convert new watering datetime to be in device time zone
        val currentZoneOffset = _state.value.deviceConfiguration?.timeZoneOffset
        if (currentZoneOffset != null) {
            val newWateringDateTime = _state.value.nextWatering!!
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .atOffset(currentZoneOffset)
                .toLocalDateTime()

            _state.value = _state.value.copy(nextWatering = newWateringDateTime)
        } else {
            // TODO: Handle exception
            Timber
                .tag("onDeviceWateringDateChanged")
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

            _state.value = _state.value.copy(nextWatering = newWateringDateTime)
        } else {
            // TODO: Handle exception
            Timber
                .tag("onDeviceWateringDateChanged")
                .e("Can't update watering time, because the current zone offset is unknown (null)")
        }
    }

    private fun getDeviceConfiguration(id: String) {
        viewModelScope.launch {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.value = _state.value.copy(
                        isLoading = true,
                        error = null
                    )
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            deviceConfiguration = ret.data,
                            isLoading = false,
                            error = null
                        )
                        _originalDeviceConfiguration.value = ret.data
                    }
                    is Result.Error -> _state.value = DeviceSettingsState(
                        error = ret.throwable.message
                    )
                }
            }
        }
    }

    private fun getDeviceNextWatering(id: String) = viewModelScope.launch {
        getDeviceUseCase.execute(id).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.value = _state.value.copy(
                    isLoading = true,
                    error = null
                )
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        nextWatering = ret.data.nextWatering,
                        isLoading = false,
                        error = null
                    )
                    _originalNextWatering.value = ret.data.nextWatering
                }
                is Result.Error -> _state.value = DeviceSettingsState(
                    error = ret.throwable.message
                )
            }
        }
    }

    private suspend fun updateDeviceConfiguration(
        id: String,
        deviceConfiguration: DeviceConfiguration,
    ) {
        updateDeviceConfigurationUseCase.execute(id, deviceConfiguration).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.value = _state.value.copy(
                    isLoading = true,
                    error = null
                )
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null
                    )
                    refreshData()
                }
                is Result.Error -> _state.value = DeviceSettingsState(
                    isLoading = false,
                    error = ret.throwable.message
                )
            }
        }
    }

    private suspend fun updateNextWatering(id: String, newWateringTime: LocalDateTime) {
        updateDeviceNextWateringUseCase.execute(id, newWateringTime).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.value = _state.value.copy(
                    isLoading = true,
                    error = null
                )
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = null
                )
                is Result.Error -> _state.value = DeviceSettingsState(
                    isLoading = false,
                    error = ret.throwable.message
                )
            }
        }
    }
}