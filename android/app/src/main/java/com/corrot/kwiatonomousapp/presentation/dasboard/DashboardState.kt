package com.corrot.kwiatonomousapp.presentation.dasboard

import com.corrot.kwiatonomousapp.domain.model.Device

data class DashboardState(
    val device: Device? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)