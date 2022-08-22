package com.corrot.kwiatonomousapp.presentation.add_user_device

import com.corrot.kwiatonomousapp.domain.model.UserDevice

data class AddEditUserDeviceState(
    val deviceId: String = "",
    val isDeviceIdValid: Boolean = false,

    val deviceName: String = "",
    val isDeviceNameValid: Boolean = false,

    val deviceImageName: String = "",

    val notificationsOn: Boolean = true,

    val isLoading: Boolean = false,
    val error: String? = null,
) {
    fun createUserDevice() = UserDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceImageName = deviceImageName,
        notificationsOn = notificationsOn
    )
}