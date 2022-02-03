package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.domain.model.UserDevice

data class DevicesState(
    val userDevices: List<UserDevice>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)