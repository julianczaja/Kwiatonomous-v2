package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.common.mapBetween
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.format.DateTimeFormatter

@Preview("DateLineChartPreviewLight", widthDp = 400, heightDp = 250, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun DateLineChartPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(250.dp)
            ) {
                DateLineChart(
                    xData = listOf(
                        1639663002L,
                        1639666602L,
                        1639670202L,
                        1639673802L,
                        1639677402L,
                        1639681002L,
                        1639684602L,
                        1639688202L,
                        1639691802L,
                        1639695402L,
                        1639699002L,
                        1639702602L,
                        1639706202L,
                        1639709802L,
                        1639713402L,
                        1639717002L,
                        1639720602L,
                        1639724202L,
                        1639727802L,
                        1639731402L,
                        1639735002L,
                        1639738602L,
                        1639742202L,
                        1639745802L,
                        1639749402L,
                    ).reversed(),
                    yData = listOf(
                        11f,
                        10f,
                        12f,
                        13f,
                        14f,
                        10f,
                        15f,
                        12f,
                        13f,
                        15f,
                        12f,
                        12f,
                        13f,
                        11f,
                        14f,
                        14f,
                        10f,
                        15f,
                        12f,
                        14f,
                        14f,
                        10f,
                        15f,
                        12f,
                        10f,
                        15f,
                    ),
                    fromDate = 1639663002L,
                    toDate = 1639749402L,
                    dateType = LineChartDateType.DAY,
                    yAxisUnit = "%",
                )
            }
        }
    }
}

@Preview("DateLineChartPreviewDark", widthDp = 400, heightDp = 200, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DateLineChartPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(250.dp)
            ) {
                DateLineChart(
                    xData = listOf(
                        1639673802L,
                        1639677402L,
                        1639681002L,
                        1639684602L,
                        1639688202L,
                        1639691802L,
                        1639695402L,
                        1639699002L,
                        1639702602L,
                        1639706202L,
                        1639709802L,
                        1639713402L,
                        1639717002L,
                        1639720602L,
                        1639724202L,
                        1639727802L,
                        1639731402L,
                    ).reversed(),
                    yData = listOf(
                        13f,
                        14f,
                        10f,
                        15f,
                        12f,
                        13f,
                        15f,
                        12f,
                        12f,
                        13f,
                        11f,
                        14f,
                        14f,
                        10f,
                        15f,
                        12f,
                        14f,
                        14f,
                    ),
                    fromDate = 1639663002L,
                    toDate = 1639749402L,
                    dateType = LineChartDateType.DAY,
                    yAxisUnit = "°C",
                    renderDropLines = true,
                    marginX = 0f,
                    marginY = 0f
                )
            }
        }
    }
}

enum class LineChartDateType {
    DAY,
    WEEK,
    MONTH
}

enum class LineChartDataType {
    TEMPERATURE,
    HUMIDITY,
    BATTERY
}

@Composable
fun DateLineChart(
    xData: List<Long>,
    yData: List<Float>,
    fromDate: Long,
    toDate: Long,
    dateType: LineChartDateType,
    yAxisUnit: String = "",
    marginX: Float = 50f,
    marginY: Float = 50f,
    lineColor: Color = MaterialTheme.colors.primary,
    renderGridLines: Boolean = true,
    renderDropLines: Boolean = false,
    isLoading: Boolean = false,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    if (xData.isEmpty() || yData.isEmpty()) {
        Text("No data", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        return
    }

    val yAxisPaint = Paint().apply {
        color = if (isDarkTheme) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        textSize = 24f
        textAlign = Paint.Align.RIGHT
    }
    val xAxisPaint = Paint().apply {
        color = if (isDarkTheme) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }
    val axisDecorationColor = if (isDarkTheme) Color.White else Color.Gray

    val xAxisDividersCount = when (dateType) {
        LineChartDateType.DAY -> 7
        LineChartDateType.WEEK -> 7
        LineChartDateType.MONTH -> 5
    }
    val yAxisDividersCount = 5

    val xValueMax = toDate
    val xValueMin = fromDate
    val yValueMax = yData.maxOrNull() ?: 0f
    val yValueMin = yData.minOrNull() ?: 0f

    val axisPaddingX = 100f
    val axisPaddingY = 50f


    Canvas(Modifier.fillMaxSize()) {
        val xMin = axisPaddingX + marginX
        val xMax = size.width - marginX
        val yMin = marginY
        val yMax = size.height - marginY - axisPaddingY

        // X axis data
        val xAxis = xData.map {
            it.toFloat().mapBetween(
                xValueMin.toFloat(), xValueMax.toFloat(),
                xMin, xMax
            )
        }

        // Y axis data
        val yAxis = yData.map {
            it.mapBetween(
                yValueMax, yValueMin,
                yMin, yMax
            )
        }

        assert(yAxis.size == xAxis.size)

        // X axis decoration
        drawLine(
            color = axisDecorationColor,
            start = Offset(axisPaddingX, size.height - axisPaddingY),
            end = Offset(size.width - marginX, size.height - axisPaddingY),
            strokeWidth = 2f
        )
        val xAxisValueStep = (xValueMax - xValueMin) / (xAxisDividersCount - 1)
        for (i in 0 until xAxisDividersCount) {
            val xCurrent = xValueMin + (xAxisValueStep * i)
            val xAxisValue = xCurrent.toFloat().mapBetween(
                xValueMin.toFloat(), xValueMax.toFloat(),
                xMin, xMax
            )
            drawLine(
                color = axisDecorationColor,
                start = Offset(xAxisValue, size.height - axisPaddingY - 10f),
                end = Offset(xAxisValue, size.height - axisPaddingY + 10f),
                strokeWidth = 2f
            )
            if (renderDropLines) {
                drawLine(
                    color = axisDecorationColor,
                    start = Offset(xAxisValue, yMin),
                    end = Offset(xAxisValue, size.height - axisPaddingY),
                    strokeWidth = 2f,
                    alpha = 0.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            }
            drawIntoCanvas {
                val xText = when (dateType) {
                    LineChartDateType.DAY -> "%02d:%02d".format(
                        xCurrent.toLocalDateTime().hour,
                        xCurrent.toLocalDateTime().minute,
                    )
                    LineChartDateType.WEEK -> xCurrent.toLocalDateTime().format(
                        DateTimeFormatter.ofPattern("E")
                    )
                    LineChartDateType.MONTH -> "%02d:%02d".format(
                        xCurrent.toLocalDateTime().dayOfMonth,
                        xCurrent.toLocalDateTime().monthValue,
                    )
                }
                it.nativeCanvas.drawText(
                    xText,
                    xAxisValue,
                    size.height - axisPaddingY + 36f,
                    xAxisPaint
                )
            }
        }


        // Y axis decoration
        drawLine(
            color = axisDecorationColor,
            start = Offset(axisPaddingX, yMin),
            end = Offset(axisPaddingX, size.height - axisPaddingY),
            strokeWidth = 2f
        )
        val yAxisValueStep = (yValueMax - yValueMin) / (yAxisDividersCount - 1)
        for (i in 0 until yAxisDividersCount) {
            val yCurrent = (yValueMin + (yAxisValueStep * i))
            val yAxisValue: Float = yCurrent.mapBetween(
                yValueMax, yValueMin,
                yMin, yMax
            )
            drawLine(
                color = axisDecorationColor,
                start = Offset(axisPaddingX - 10f, yAxisValue),
                end = Offset(axisPaddingX + 10f, yAxisValue),
                strokeWidth = 2f
            )
            if (renderGridLines) {
                drawLine(
                    color = axisDecorationColor,
                    start = Offset(axisPaddingX + 20f, yAxisValue),
                    end = Offset(size.width - marginX, yAxisValue),
                    strokeWidth = 2f,
                    alpha = 0.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            }

            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "%.1f%s".format(yCurrent, yAxisUnit),
                    axisPaddingX - 24f,
                    yAxisValue,
                    yAxisPaint
                )
            }
        }

        if (isLoading) {
            return@Canvas
        }

        // Data fill
        val path = Path()
        path.moveTo(xMax, yMax)
        for (i in xAxis.indices) {
            path.lineTo(xAxis[i], yAxis[i])
        }
        path.lineTo(xMin, yMax)
        drawPath(path, color = lineColor, alpha = 0.2f)

        // Data line
        var lastPoint = Offset.Unspecified
        for (i in xAxis.indices) {
            if (i == 0) {
                lastPoint = Offset(xAxis[0], yAxis[0])
            } else {
                val newPoint = Offset(xAxis[i], yAxis[i])
                drawLine(
                    start = lastPoint,
                    end = newPoint,
                    color = lineColor,
                    strokeWidth = 2f
                )
                lastPoint = newPoint.copy()
            }
        }
    }
}