package com.corrot.kwiatonomousapp.presentation.add_user_device

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Constants
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.usecase.AddUserDeviceUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDeviceUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateUserDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddEditUserDeviceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addUserDeviceUseCase: AddUserDeviceUseCase,
    private val updateUserDeviceUseCase: UpdateUserDeviceUseCase,
    private val getUserDeviceUseCase: GetUserDeviceUseCase
) : ViewModel() {

    enum class Event {
        NAVIGATE_UP
    }

    val state = mutableStateOf(AddEditUserDeviceState())
    val eventFlow = MutableSharedFlow<Event>()
    val isEditMode = savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID) != null

    init {
        if (isEditMode) {
            val deviceId = savedStateHandle.get<String>(Constants.NAV_ARG_DEVICE_ID)!!

            state.value = state.value.copy(isLoading = true)

            viewModelScope.launch(Dispatchers.IO) {
                getUserDeviceUseCase.execute(deviceId).collect { ret ->
                    withContext(Dispatchers.Main) {
                        when (ret) {
                            is Result.Loading -> {
                                state.value = state.value.copy(isLoading = true)
                            }
                            is Result.Success -> {
                                state.value = state.value.copy(
                                    isLoading = false,
                                    deviceId = ret.data.deviceId,
                                    isDeviceIdValid = true,
                                    deviceName = ret.data.deviceName,
                                    isDeviceNameValid = true,
                                    deviceImageId = ret.data.deviceImageId

                                )
                            }
                            is Result.Error -> {
                                state.value = state.value.copy(
                                    isLoading = false,
                                    error = ret.throwable.message ?: "Unknown error"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onDeviceIdChanged(deviceId: String) {
        state.value = state.value.copy(
            deviceId = deviceId,
            isDeviceIdValid = isDeviceIdValid(deviceId)
        )
    }

    fun onDeviceNameChanged(deviceName: String) {
        state.value = state.value.copy(
            deviceName = deviceName,
            isDeviceNameValid = isDeviceNameValid(deviceName)
        )
    }

    fun onDeviceImageIdChanged(deviceImageId: Int) {
        state.value = state.value.copy(
            deviceImageId = deviceImageId
        )
    }

    fun confirmError() = viewModelScope.launch {
        if (isEditMode) {
            eventFlow.emit(Event.NAVIGATE_UP)
        } else {
            state.value = state.value.copy(error = null)
        }
    }

    fun onDoneClicked() = viewModelScope.launch {
        if (isEditMode) {
            updateUserDevice()
        } else {
            addNewUserDevice()
        }
    }

    private suspend fun updateUserDevice() {
        val newUserDevice = UserDevice(
            deviceId = state.value.deviceId,
            deviceName = state.value.deviceName,
            deviceImageId = state.value.deviceImageId
        )
        updateUserDeviceUseCase.execute(newUserDevice).collect { ret ->
            withContext(Dispatchers.Main) {
                when (ret) {
                    is Result.Loading -> {
                        state.value = state.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        state.value = state.value.copy(isLoading = false)
                        eventFlow.emit(Event.NAVIGATE_UP)
                    }
                    is Result.Error -> {
                        state.value = state.value.copy(
                            isLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    private suspend fun addNewUserDevice() {
        val newUserDevice = UserDevice(
            deviceId = state.value.deviceId,
            deviceName = state.value.deviceName,
            deviceImageId = state.value.deviceImageId
        )
        addUserDeviceUseCase.execute(newUserDevice).collect { ret ->
            withContext(Dispatchers.Main) {
                when (ret) {
                    is Result.Loading -> {
                        state.value = state.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        state.value = state.value.copy(isLoading = false)
                        eventFlow.emit(Event.NAVIGATE_UP)
                    }
                    is Result.Error -> {
                        state.value = state.value.copy(
                            isLoading = false,
                            error = ret.throwable.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }

    // TODO: Move validation layer below (???)
    private fun isDeviceIdValid(deviceId: String) = deviceId.matches(Regex("^[A-Za-z]{10}\$"))

    private fun isDeviceNameValid(deviceName: String) = deviceName.length in 1..24

}