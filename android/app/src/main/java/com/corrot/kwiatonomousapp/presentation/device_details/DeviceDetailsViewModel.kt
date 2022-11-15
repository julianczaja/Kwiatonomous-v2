package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
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
    val state = mutableStateOf(DeviceDetailsState())
    val eventFlow = MutableSharedFlow<Event>()
    val currentAppTheme = appPreferencesRepository.getAppTheme()
    val currentChartSettings = appPreferencesRepository.getChartSettings()

    // Keep track of jobs to cancel it first when refreshData() is triggered
    // otherwise there will be multiple coroutines in background
    // TODO: is that a good approach
    private var getDeviceJob: Job? = null
    private var getDeviceUpdatesJob: Job? = null
    private var getDeviceConfigurationJob: Job? = null
    private var getDeviceEventsJob: Job? = null

    private var selectedDeviceEvent: DeviceEvent? = null

    val isLoading: Boolean
        get() = state.value.isUserDeviceLoading
                || state.value.isDeviceLoading
                || state.value.isDeviceUpdatesLoading
                || state.value.isDeviceConfigurationLoading
                || state.value.isDeviceEventsLoading

    init {
        state.value = state.value.copy(
            selectedDateRange = calculateDateRange(LineChartDateType.DAY)
        )
        deviceId?.let { getUserDevice(it) }
        refreshData()
    }

    fun refreshData() {
        deviceId?.let {
            getDevice(it)
            getDeviceConfiguration(it)
            getDeviceUpdates(
                id = it,
                from = state.value.selectedDateRange.first,
                to = state.value.selectedDateRange.second
            )
            getDeviceEvents(it)
        }
    }

    fun confirmError() {
        state.value = state.value.copy(error = null)
    }


    fun toggleDeviceNotifications() = viewModelScope.launch(ioDispatcher) {
        state.value.userDevice.let { currentUserDevice ->
            if (currentUserDevice != null) {
                updateUserDeviceUseCase.execute(
                    currentUserDevice.copy(notificationsOn = !currentUserDevice.notificationsOn)
                ).collect { ret ->
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> {
                                state.value = state.value.copy(isUserDeviceLoading = true)
                            }
                            is Result.Success -> {
                                state.value = state.value.copy(isUserDeviceLoading = false)
                            }
                            is Result.Error -> {
                                state.value = state.value.copy(
                                    isUserDeviceLoading = false,
                                    error = ret.throwable.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            } else {
                state.value = state.value.copy(
                    isUserDeviceLoading = false,
                    error = "Unknown error"
                )
            }
        }
    }

    fun onUserDeviceAction(action: UserDeviceAction) = viewModelScope.launch(ioDispatcher) {
        when (action) {
            UserDeviceAction.EDIT -> {
                eventFlow.emit(Event.OPEN_EDIT_USER_DEVICE_SCREEN)
            }
            UserDeviceAction.DELETE -> {
                eventFlow.emit(Event.SHOW_DELETE_ALERT_DIALOG)
            }
        }
    }

    fun deleteUserDevice() = viewModelScope.launch(ioDispatcher) {
        deleteUserDeviceUseCase.execute(state.value.userDevice!!).collect { ret ->
            withContext(Dispatchers.Main) {
                when (ret) {
                    is Result.Loading -> {
                        state.value = state.value.copy(isUserDeviceLoading = true)
                    }
                    is Result.Success -> {
                        eventFlow.emit(Event.NAVIGATE_UP)
                    }
                    is Result.Error -> {
                        state.value = state.value.copy(
                            isUserDeviceLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private fun getUserDevice(deviceId: String) = viewModelScope.launch(ioDispatcher) {
        getUserDeviceUseCase.execute(deviceId).collect { ret ->
            withContext(Dispatchers.Main) {
                when (ret) {
                    is Result.Loading -> {
                        state.value = state.value.copy(isUserDeviceLoading = true)
                    }
                    is Result.Success -> {
                        state.value = state.value.copy(
                            isUserDeviceLoading = false,
                            userDevice = ret.data
                        )
                    }
                    is Result.Error -> {
                        state.value = state.value.copy(
                            isUserDeviceLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private fun getDevice(id: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceJob?.cancelAndJoin()
        getDeviceJob = viewModelScope.launch(ioDispatcher) {
            getDeviceUseCase.execute(id).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceLoading = true,
                            device = ret.data
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceLoading = false,
                            device = ret.data/*, error = null*/
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private fun getDeviceUpdates(id: String, from: Long, to: Long) =
        viewModelScope.launch(ioDispatcher) {
            getDeviceUpdatesJob?.cancelAndJoin()
            getDeviceUpdatesJob = viewModelScope.launch(ioDispatcher) {
                getDeviceUpdatesByDateUseCase.execute(id, from, to).collect { ret ->
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> state.value = state.value.copy(
                                isDeviceUpdatesLoading = true,
                                deviceUpdates = ret.data
                            )
                            is Result.Success -> state.value = state.value.copy(
                                isDeviceUpdatesLoading = false,
                                deviceUpdates = ret.data/*, error = null*/
                            )
                            is Result.Error -> state.value = state.value.copy(
                                isDeviceUpdatesLoading = false,
                                error = ret.throwable.message ?: "Unknown error"
                            )
                        }
                    }
                }
            }
        }

    private fun getDeviceConfiguration(id: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceConfigurationJob?.cancelAndJoin()
        getDeviceConfigurationJob = viewModelScope.launch(ioDispatcher) {
            getDeviceConfigurationUseCase.execute(id).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceConfigurationLoading = true,
                            deviceConfiguration = ret.data
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceConfigurationLoading = false,
                            deviceConfiguration = ret.data/*, error = null*/
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceConfigurationLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private fun getDeviceEvents(deviceId: String) = viewModelScope.launch(ioDispatcher) {
        getDeviceEventsJob?.cancelAndJoin()
        getDeviceEventsJob = viewModelScope.launch(ioDispatcher) {
            getAllDeviceEventsUseCase.execute(deviceId, 100).collect { ret ->
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceEventsLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            deviceEvents = ret.data.sortedByDescending { it.timestamp },
                            isDeviceEventsLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceEventsLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    fun onChartDateTypeSelected(dateType: LineChartDateType) = viewModelScope.launch(ioDispatcher) {
        when {
            dateType.ordinal < state.value.selectedChartDateType.ordinal -> {
                state.value.deviceUpdates?.let { oldDeviceUpdates ->
                    val newDataRange = calculateDateRange(dateType)
                    val newDeviceUpdates = oldDeviceUpdates.filter {
                        it.updateTime.toLong() in newDataRange.first..newDataRange.second
                    }
                    withContext(Dispatchers.Main) {
                        state.value = state.value.copy(
                            deviceUpdates = newDeviceUpdates,
                            selectedChartDateType = dateType,
                            selectedDateRange = newDataRange
                        )
                    }
                }
            }
            dateType.ordinal > state.value.selectedChartDateType.ordinal -> {
                val newDataRange = calculateDateRange(dateType)
                deviceId?.let {
                    getDeviceUpdates(
                        id = it,
                        from = newDataRange.first,
                        to = newDataRange.second
                    )
                }
                withContext(Dispatchers.Main) {
                    state.value = state.value.copy(
                        selectedChartDateType = dateType,
                        selectedDateRange = newDataRange
                    )
                }
            }
            dateType.ordinal == state.value.selectedChartDateType.ordinal -> {}
        }
    }


    fun onChartDataTypeSelected(dataType: LineChartDataType) {
        state.value = state.value.copy(selectedChartDataType = dataType)
    }

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

    fun onNoteTitleChanged(newNoteTitle: String) {
        state.value = state.value.copy(noteTitle = newNoteTitle)
    }

    fun onNoteContentChanged(newNoteContent: String) {
        state.value = state.value.copy(noteContent = newNoteContent)
    }

    fun onAddNoteClicked() {
        deviceId?.let { deviceId ->
            viewModelScope.launch(ioDispatcher) {
                val user = userRepository.getCurrentUserFromDatabase().first()
                    ?: throw Exception("There is no logged in user")

                val deviceEvent = DeviceEvent.UserNote(
                    userName = user.userName,
                    title = state.value.noteTitle,
                    content = state.value.noteContent,
                    deviceId = deviceId,
                    timestamp = LocalDateTime.now()
                )
                addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceEventsLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceEventsLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceEventsLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    fun onAddWateringEventClicked() {
        deviceId?.let { deviceId ->
            viewModelScope.launch(ioDispatcher) {
                val deviceEvent = DeviceEvent.Watering(
                    deviceId = deviceId,
                    timestamp = LocalDateTime.now()
                )
                addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceEventsLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceEventsLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceEventsLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    fun onAddPumpCleaningEventClicked() {
        deviceId?.let { deviceId ->
            viewModelScope.launch(ioDispatcher) {
                // TODO: Combine these 2 usecases
                updateDeviceLastPumpCleaningUseCase.execute(deviceId, LocalDateTime.now())
                    .onEach { if (it is Result.Error) throw it.throwable }
                    .collect()

                val deviceEvent = DeviceEvent.PumpCleaning(
                    deviceId = deviceId,
                    timestamp = LocalDateTime.now()
                )
                addDeviceEventUseCase.execute(deviceEvent).collect { ret ->
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceEventsLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceEventsLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceEventsLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
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
                withContext(Dispatchers.Main) {
                    when (ret) {
                        is Result.Loading -> state.value = state.value.copy(
                            isDeviceEventsLoading = true,
                            error = null
                        )
                        is Result.Success -> state.value = state.value.copy(
                            isDeviceEventsLoading = false
                        )
                        is Result.Error -> state.value = state.value.copy(
                            isDeviceEventsLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                    selectedDeviceEvent = null
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    error = "There is no selected event" // FIXME
                )
            }
        }
    }
}

