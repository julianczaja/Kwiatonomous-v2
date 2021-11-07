package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.domain.model.Device

data class DevicesState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)