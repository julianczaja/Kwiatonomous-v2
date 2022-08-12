package com.corrot.kwiatonomousapp.presentation.device_details

import com.corrot.kwiatonomousapp.common.components.chart.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDateType
import com.corrot.kwiatonomousapp.domain.model.*

data class DeviceDetailsState(
    val userDevice: UserDevice? = null,
    val isUserDeviceLoading: Boolean = false,

    val device: Device? = null,
    val isDeviceLoading: Boolean = false,

    val deviceUpdates: List<DeviceUpdate>? = null,
    val isDeviceUpdatesLoading: Boolean = false,

    val deviceConfiguration: DeviceConfiguration? = null,
    val isDeviceConfigurationLoading: Boolean = false,

    val deviceEvents: List<DeviceEvent>? = null,
    val isDeviceEventsLoading: Boolean = false,

    val error: String? = null,

    val selectedChartDateType: LineChartDateType = LineChartDateType.DAY,
    val selectedChartDataType: LineChartDataType = LineChartDataType.TEMPERATURE,
    val selectedDateRange: Pair<Long, Long> = (Pair(0L, 0L)),

    val noteTitle: String = "",
    val noteContent: String = "",
)