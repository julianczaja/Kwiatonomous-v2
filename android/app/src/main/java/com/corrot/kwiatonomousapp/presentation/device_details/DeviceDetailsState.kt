package com.corrot.kwiatonomousapp.presentation.device_details

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.components.LineChartDataType
import com.corrot.kwiatonomousapp.common.components.LineChartDateType
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate

data class DeviceDetailsState(
    val device: Result<Device> = Result.Loading,
    val deviceUpdates: Result<List<DeviceUpdate>> = Result.Loading,
    val deviceConfiguration: Result<DeviceConfiguration?> = Result.Loading,
    val selectedChartDateType: LineChartDateType = LineChartDateType.DAY,
    val selectedChartDataType: LineChartDataType = LineChartDataType.TEMPERATURE,
    val selectedDateRange: Pair<Long, Long> = (Pair(0L, 0L))
)