package com.corrot.kwiatonomousapp.presentation.add_user_device

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import com.corrot.kwiatonomousapp.domain.usecase.CheckIfDeviceExistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserDeviceViewModel @Inject constructor(
    private val checkIfDeviceExistsUseCase: CheckIfDeviceExistsUseCase,
    private val userDeviceRepository: UserDeviceRepository
) : ViewModel() {

    enum class Event {
        NAVIGATE_UP
    }

    val state = mutableStateOf(AddUserDeviceState())
    val eventFlow = MutableSharedFlow<Event>()

    fun confirmError() {
        state.value = state.value.copy(error = null)
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

    fun onDoneClicked() = viewModelScope.launch {
        checkIfDeviceExistsUseCase.execute(state.value.deviceId).collect { ret ->
            when (ret) {
                is Result.Loading -> state.value = state.value.copy(
                    isLoading = true, error = null
                )
                is Result.Success -> {
                    val deviceExists = ret.data
                    if (deviceExists) {
                        try {
                            userDeviceRepository.addUserDevice(
                                UserDevice(
                                    deviceId = state.value.deviceId,
                                    deviceName = state.value.deviceName,
                                    deviceImageId = state.value.deviceImageId
                                )
                            )
                            eventFlow.emit(Event.NAVIGATE_UP)
                        } catch (e: Exception) {
                            state.value = state.value.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    } else {
                        state.value = state.value.copy(
                            isLoading = false,
                            error = "Device with ID \"${state.value.deviceId}\" doesn't exist"
                        )
                    }
                }
                is Result.Error -> state.value = state.value.copy(
                    isLoading = false, error = ret.throwable.message
                )
            }
        }
    }

    // TODO: Move validation layer below (???)
    private fun isDeviceIdValid(deviceId: String) = deviceId.matches(Regex("^[A-Za-z]{10}\$"))

    private fun isDeviceNameValid(deviceName: String) = deviceName.length in 1..24
}