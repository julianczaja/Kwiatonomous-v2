package com.corrot.kwiatonomousapp.presentation.device_settings

import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import java.time.LocalDateTime

data class DeviceSettingsState(
    val deviceConfiguration: DeviceConfiguration? = null,
    val nextWatering: LocalDateTime? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)