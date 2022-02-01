package com.corrot.kwiatonomousapp.presentation.device_details

import com.corrot.kwiatonomousapp.common.components.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.LineChartDateType
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

data class DeviceDetailsState(
    val device: Device? = null,
    val isDeviceLoading: Boolean = false,
//    val deviceError: String? = null,

    val deviceUpdates: List<DeviceUpdate>? = null,
    val isDeviceUpdatesLoading: Boolean = false,
//    val deviceUpdatesError: String? = null,

    val deviceConfiguration: DeviceConfiguration? = null,
    val isDeviceConfigurationLoading: Boolean = false,
//    val deviceConfigurationError: String? = null,

    val error: String? = null,

    val selectedChartDateType: LineChartDateType = LineChartDateType.DAY,
    val selectedChartDataType: LineChartDataType = LineChartDataType.TEMPERATURE,
    val selectedDateRange: Pair<Long, Long> = (Pair(0L, 0L))
)