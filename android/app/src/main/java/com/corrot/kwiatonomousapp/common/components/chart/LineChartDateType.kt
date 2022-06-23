package com.corrot.kwiatonomousapp.common.components.chart

import android.content.Context
import com.corrot.kwiatonomousapp.R

enum class LineChartDateType {
    DAY,
    WEEK,
    MONTH
}

fun LineChartDateType.mapToString(context: Context): String = when (this) {
    LineChartDateType.DAY -> context.getString(R.string.day)
    LineChartDateType.WEEK -> context.getString(R.string.week)
    LineChartDateType.MONTH -> context.getString(R.string.month)
}