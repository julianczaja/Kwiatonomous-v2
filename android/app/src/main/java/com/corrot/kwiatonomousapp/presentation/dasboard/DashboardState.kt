package com.corrot.kwiatonomousapp.presentation.dasboard

import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

data class DashboardState(
    val device: Device? = null,
    val deviceUpdates: List<DeviceUpdate> = emptyList(),
    val deviceConfiguration: DeviceConfiguration? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)