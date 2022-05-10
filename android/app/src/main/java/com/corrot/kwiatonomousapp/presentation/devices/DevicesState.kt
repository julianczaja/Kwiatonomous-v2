package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice

data class DevicesState(
    val userDevicesWithLastUpdates: List<Pair<UserDevice, DeviceUpdate?>>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)