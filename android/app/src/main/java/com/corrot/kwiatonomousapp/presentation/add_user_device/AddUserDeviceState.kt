package com.corrot.kwiatonomousapp.presentation.add_user_device

data class AddUserDeviceState(
    val deviceId: String = "",
    val isDeviceIdValid: Boolean = false,

    val deviceName: String = "",
    val isDeviceNameValid: Boolean = false,

    val deviceImageId: Int = 0,

    val isLoading: Boolean = false,
    val error: String? = null
)