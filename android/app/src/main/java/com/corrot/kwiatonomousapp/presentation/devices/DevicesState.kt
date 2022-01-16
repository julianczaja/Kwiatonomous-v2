package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.domain.model.Device

data class DevicesState(
    val devices: List<Device>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)