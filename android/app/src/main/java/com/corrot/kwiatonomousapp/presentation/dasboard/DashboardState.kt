package com.corrot.kwiatonomousapp.presentation.dasboard

import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.User

data class DashboardState(
    val user: User? = null,
    val events: List<DeviceEvent>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)