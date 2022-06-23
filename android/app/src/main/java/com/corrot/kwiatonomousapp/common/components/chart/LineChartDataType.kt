package com.corrot.kwiatonomousapp.common.components.chart

import android.content.Context
import com.corrot.kwiatonomousapp.R

enum class LineChartDataType {
    TEMPERATURE,
    HUMIDITY,
    BATTERY
}

fun LineChartDataType.mapToString(context: Context): String = when (this) {
    LineChartDataType.BATTERY -> context.getString(R.string.battery)
    LineChartDataType.HUMIDITY -> context.getString(R.string.humidity_abbr)
    LineChartDataType.TEMPERATURE -> context.getString(R.string.temperature_abbr)
}