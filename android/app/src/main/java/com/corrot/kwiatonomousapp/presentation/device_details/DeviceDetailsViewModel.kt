package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.DAY_SECONDS
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDateType
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.UserDeviceAction
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DeviceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    appPreferencesRepository: AppPreferencesRepository,
    private val userRepository: UserRepository,
    private val getDeviceUseCase: GetDeviceUseCase,
    private val getDeviceUpdatesByDateUseCase: GetDeviceUpdatesByDateUseCase,
    private val getDeviceConfigurationUseCase: GetDeviceConfigurationUseCase,
    private val getUserDeviceUseCase: GetUserDeviceUseCase,
    private val updateUserDeviceUseCase: UpdateUserDeviceUseCase,
    private val deleteUserDeviceUseCase: DeleteUserDeviceUseCase,
    private val getAllDeviceEventsUseCase: GetAllDeviceEventsUseCase,
    private val addDeviceEventUseCase: AddDeviceEventUseCase,
    private val deleteDeviceEventUseCase: DeleteDeviceEventUseCase,
    private val updateDeviceLastPumpCleaningUseCase: UpdateDeviceLastPumpCleaningUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    enum class Event {
        NAVIGATE_UP,
        SHOW_DELETE_ALERT_DIALOG,
        OPEN_EDIT_USER_DEVICE_SCREEN
    }

    private val deviceId = savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)
    private val _state = MutableStateFlow(DeviceDetailsState(selectedDateRange = calculateDateRange(LineChartDateType.DAY)))
    val state: StateFlow<DeviceDetailsState> = _state
    val eventFlow = MutableSharedFlow<Event>()
    val currentAppTheme = appPreferencesRepository.getAppTheme()
    val currentChartSettings = appPreferencesRepository.getChartSettings()

    // Keep track of jobs to cancel it first when refreshData() is triggered
    // otherwise there will be multiple coroutines in background
    // TODO: is that a good approach ?
    private var getDeviceJob: Job? = null
    private var getDeviceUpdatesJob: Job? = null
    private var getDeviceConfigurationJob: Job? = null
    private var getDeviceEventsJob: Job? = null

    private var selectedDeviceEvent: DeviceEvent? = null

    val isLoading: Boolean
        get() = _state.value.isUserDeviceLoading
                || _state.value.isDeviceLoading
                || _state.value.isDeviceUpdatesLoading
                || _state.value.isDeviceConfigurationLoading
                || _state.value.isDeviceEventsLoading

    init {
        deviceId?.let { getUserDevice(it) }
        refreshData()
    }

    fun refreshData() {
        if (deviceId.isNullOrEmpty()) {
            _state.update { DeviceDetailsState(error = "Unknown error") }
        } else {
            getDevice(deviceId)
            getDeviceConfiguration(deviceId)
            getDeviceUpdates(
                deviceId = deviceId,
                from = _state.value.selectedDateRange.first,
                to = _state.value.selectedDateRange.second
            )
            getDeviceEvents(deviceId)
        }
    }

    fun confirmError() = _state.update { it.copy(error = null) }

    fun toggleDeviceNotifications() = _state.value.userDevice.let { currentUserDevice ->
        if (currentUserDevice != null) {
            viewModelScope.launch(ioDispatcher) {
                updateUserDeviceUseCase.execute(currentUserDevice.copy(notificationsOn = !currentUserDevice.notificationsOn))
                    .collect { ret ->
                        when (ret) {
                            is Result.Loading -> _state.update { it.copy(isUserDeviceLoading = true) }
                            is Result.Success -> _state.update { it.copy(isUserDeviceLoading = false) }
                            is Result.Error -> _state.update {
                                it.copy(isUserDeviceLoading = false, error = ret.throwable.message ?: "Unknown error")
                            }
                        }
                    }
            }
        } else {
            _state.update { it.copy(isUserDeviceLoading = false, error = "Unknown error") }
        }
    }


    fun onUserDeviceAction(action: UserDeviceAction) = viewModelScope.launch(ioDispatcher) {
        when (action) {
            UserDeviceAction.EDIT -> eventFlow.emit(Event.OPEN_EDIT_USER_DEVICE_SCREEN)
            UserDeviceAction.DELETE -> eventFlow.emit(Event.SHOW_DELETE_ALERT_DIALOG)
        }
    }

    fun deleteUserDevice() = viewModelScope.launch(ioDispatcher) {
        deleteUserDeviceUseCase.execute(state.value.userDevice!!).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isUserDeviceLoading = true, userDevice = ret.data) }
                is Result.Success -> eventFlow.emit(Event.NAVIGATE_UP)
                is Result.Error -> _state.update { it.copy(isUserDeviceLoading = false, error = ret.throwable.message ?: "Unknown error") }
            }
        }
    }

    private fun getUserDevice(deviceId: String) = viewModelScope.launch(ioDispatcher) {
        getUserDeviceUseCase.execute(deviceId).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isUserDeviceLoading = true, userDevice = ret.data) }
                is Result.Success -> _state.update { it.copy(isUserDeviceLoading = false, userDevice = ret.data) }
                is Result.Error -> _state.update { it.copy(isUserDeviceLoading = false, error = ret.throwable.message ?: "Unknown error") }
            }
        }
    }

    private fun getDevice(deviceId: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceJob?.cancelAndJoin()
        getDeviceJob = viewModelScope.launch(ioDispatcher) {
            getDeviceUseCase.execute(deviceId).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> _state.update { it.copy(isDeviceLoading = true, device = ret.data) }
                        is Result.Success -> _state.update { it.copy(isDeviceLoading = false, device = ret.data) }
                        is Result.Error -> _state.update {
                            it.copy(isDeviceLoading = false, error = ret.throwable.message ?: "Unknown error")
                        }
                    }
                }
            }
        }
    }

    private fun getDeviceUpdates(deviceId: String, from: Long, to: Long) = viewModelScope.launch(ioDispatcher) {
        getDeviceUpdatesJob?.cancelAndJoin()
        getDeviceUpdatesJob = viewModelScope.launch(ioDispatcher) {
            getDeviceUpdatesByDateUseCase.execute(deviceId, from, to).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isDeviceUpdatesLoading = true, deviceUpdates = ret.data) }
                    is Result.Success -> _state.update { it.copy(isDeviceUpdatesLoading = false, deviceUpdates = ret.data) }
                    is Result.Error -> _state.update {
                        it.copy(isDeviceUpdatesLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun getDeviceConfiguration(id: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceConfigurationJob?.cancelAndJoin()
        getDeviceConfigurationJob = viewModelScope.launch(ioDispatcher) {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isDeviceConfigurationLoading = true, deviceConfiguration = ret.data) }
                    is Result.Success -> _state.update { it.copy(isDeviceConfigurationLoading = false, deviceConfiguration = ret.data) }
                    is Result.Error -> _state.update {
                        it.copy(isDeviceConfigurationLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun getDeviceEvents(deviceId: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceEventsJob?.cancelAndJoin()
        getDeviceEventsJob = viewModelScope.launch(ioDispatcher) {
            getAllDeviceEventsUseCase.execute(deviceId, 100).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isDeviceEventsLoading = true, deviceEvents = ret.data) }
                    is Result.Success -> _state.update {
                        it.copy(deviceEvents = ret.data.sortedByDescending { event -> event.timestamp }, isDeviceEventsLoading = false)
                    }
                    is Result.Error -> _state.update {
                        it.copy(isDeviceEventsLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun onChartDateTypeSelected(dateType: LineChartDateType) = viewModelScope.launch(ioDispatcher) {
        when {
            dateType.ordinal < _state.value.selectedChartDateType.ordinal -> {
                _state.value.deviceUpdates?.let { oldDeviceUpdates ->
                    val newDataRange = calculateDateRange(dateType)
                    val newDeviceUpdates = oldDeviceUpdates.filter {
                        it.updateTime.toLong() in newDataRange.first..newDataRange.second
                    }
                    _state.update {
                        it.copy(deviceUpdates = newDeviceUpdates, selectedChartDateType = dateType, selectedDateRange = newDataRange)
                    }
                }
            }
            dateType.ordinal > _state.value.selectedChartDateType.ordinal -> {
                val newDataRange = calculateDateRange(dateType)
                getDeviceUpdates(deviceId = deviceId!!, from = newDataRange.first, to = newDataRange.second)
                _state.update { it.copy(selectedChartDateType = dateType, selectedDateRange = newDataRange) }
            }
            dateType.ordinal == _state.value.selectedChartDateType.ordinal -> {}
        }
    }


    fun onChartDataTypeSelected(dataType: LineChartDataType) = _state.update { it.copy(selectedChartDataType = dataType) }

    private fun calculateDateRange(dateType: LineChartDateType): Pair<Long, Long> {
        val from: Long
        val to: Long
        val currentDateTime = LocalDateTime.now()
        val midnightTomorrow = currentDateTime
            .withHour(0)
            .withMinute(0)
            .plusDays(1L)
            .toLong()

        when (dateType) {
            LineChartDateType.DAY -> {
                from = midnightTomorrow - DAY_SECONDS
                to = midnightTomorrow
            }
            LineChartDateType.WEEK -> {
                from = midnightTomorrow - (6 * DAY_SECONDS)
                to = midnightTomorrow
            }
            LineChartDateType.MONTH -> {
                val daysInMonth = currentDateTime.month.maxLength()
                from = midnightTomorrow - (daysInMonth * DAY_SECONDS)
                to = midnightTomorrow
            }
        }
        return Pair(from, to)
    }

    fun onNoteTitleChanged(newNoteTitle: String) = _state.update { it.copy(noteTitle = newNoteTitle) }

    fun onNoteContentChanged(newNoteContent: String) = _state.update { it.copy(noteContent = newNoteContent) }

    fun addNote() {
        viewModelScope.launch(ioDispatcher) {
            val user = userRepository.getCurrentUserFromDatabase().first() ?: throw Exception("There is no logged in user")

            val deviceEvent = DeviceEvent.UserNote(
                userName = user.userName,
                title = state.value.noteTitle,
                content = state.value.noteContent,
                deviceId = deviceId!!,
                timestamp = LocalDateTime.now()
            )
            addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isDeviceEventsLoading = true) }
                    is Result.Success -> _state.update { it.copy(isDeviceEventsLoading = false) }
                    is Result.Error -> _state.update {
                        it.copy(isDeviceEventsLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun addWateringEvent() = viewModelScope.launch(ioDispatcher) {
        val deviceEvent = DeviceEvent.Watering(deviceId = deviceId!!, timestamp = LocalDateTime.now())

        addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isDeviceEventsLoading = true) }
                is Result.Success -> _state.update { it.copy(isDeviceEventsLoading = false) }
                is Result.Error -> _state.update {
                    it.copy(isDeviceEventsLoading = false, error = ret.throwable.message ?: "Unknown error")
                }
            }
        }
    }

    fun addPumpCleaningEvent() = viewModelScope.launch(ioDispatcher) {
        // TODO: Combine these 2 use-cases
        updateDeviceLastPumpCleaningUseCase.execute(deviceId = deviceId!!, lastPumpCleaning = LocalDateTime.now())
            .onEach { if (it is Result.Error) throw it.throwable }
            .collect()

        val deviceEvent = DeviceEvent.PumpCleaning(deviceId = deviceId, timestamp = LocalDateTime.now())

        addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
            when (ret) {
                is Result.Loading -> _state.update { it.copy(isDeviceEventsLoading = true) }
                is Result.Success -> _state.update { it.copy(isDeviceEventsLoading = false) }
                is Result.Error -> _state.update {
                    it.copy(isDeviceEventsLoading = false, error = ret.throwable.message ?: "Unknown error")
                }
            }
        }
    }

    fun selectEventToDelete(deviceEvent: DeviceEvent) {
        selectedDeviceEvent = deviceEvent
    }

    fun deleteSelectedUserEvent() = viewModelScope.launch(ioDispatcher) {
        if (selectedDeviceEvent != null) {
            deleteDeviceEventUseCase.execute(selectedDeviceEvent!!).collect { ret ->
                when (ret) {
                    is Result.Loading -> _state.update { it.copy(isDeviceEventsLoading = true) }
                    is Result.Success -> _state.update { it.copy(isDeviceEventsLoading = false) }
                    is Result.Error -> _state.update {
                        it.copy(isDeviceEventsLoading = false, error = ret.throwable.message ?: "Unknown error")
                    }
                }
                selectedDeviceEvent = null
            }
        } else {
            _state.update { it.copy(error = "There is no selected event") }
        }
    }
}
