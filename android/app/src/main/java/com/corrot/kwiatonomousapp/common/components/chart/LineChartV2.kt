package com.corrot.kwiatonomousapp.common.components.chart

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChartProperties.AXIS_LINE_STROKE_WIDTH
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChartProperties.AXIS_PADDING_X
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChartProperties.AXIS_PADDING_Y
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChartProperties.DATA_FILL_ALPHA
import com.corrot.kwiatonomousapp.common.components.chart.DateLineChartProperties.DATA_LINE_STROKE_WIDTH
import com.corrot.kwiatonomousapp.common.mapBetween
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

object DateLineChartProperties {
    const val AXIS_PADDING_X = 100f
    const val AXIS_PADDING_Y = 50f
    const val AXIS_LINE_STROKE_WIDTH = 2f
    const val DATA_LINE_STROKE_WIDTH = 2f
    const val DATA_FILL_ALPHA = 0.2f
}

@Composable
fun DateLineChart(
    xData: List<Long>,
    yData: List<Float>,
    fromDate: Long,
    toDate: Long,
    dateType: LineChartDateType,
    dataType: LineChartDataType,
    marginX: Float = 50f,
    marginY: Float = 50f,
    textSize: Float = 24f,
    lineColor: Color = MaterialTheme.colors.primary,
    renderGridLines: Boolean = true,
    renderDropLines: Boolean = false,
    isDarkTheme: Boolean
) {
    if (xData.isEmpty() || yData.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No data",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5
            )
        }
        return
    }

    val yAxisPaint = Paint().also { paint ->
        paint.color = if (isDarkTheme) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
    }
    val xAxisPaint = Paint().also { paint ->
        paint.color = if (isDarkTheme) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER
    }
    val axisDecorationColor = if (isDarkTheme) Color.White else Color.Gray

    val xAxisDividersCount = when (dateType) {
        LineChartDateType.DAY -> 7
        LineChartDateType.WEEK -> 7
        LineChartDateType.MONTH -> 6
    }
    val yAxisDividersCount = 5

    val xValueMax = toDate
    val xValueMin = fromDate
    var yValueMax = yData.maxOrNull() ?: 0f
    var yValueMin = yData.minOrNull() ?: 0f

    // Fix for a bug with no data line when all values are have the same value
    if (yValueMin == yValueMax) {
        yValueMax += yValueMax * 0.3f
        yValueMin -= yValueMin * 0.3f
    }

    val alphaAnimatable = remember {
        Animatable(1f)
    }

    val lineColorAnimatable = remember {
        Animatable(Color.Gray, typeConverter = Color.VectorConverter(ColorSpaces.LinearSrgb))
    }

    LaunchedEffect(null) {
        launch {
            alphaAnimatable.animateTo(1f, animationSpec = tween(1000))
        }
        launch {
            lineColorAnimatable.animateTo(lineColor, animationSpec = tween(1500))
        }
    }


    Canvas(
        modifier = Modifier
            .alpha(alpha = alphaAnimatable.value)
            .fillMaxSize()
    ) {
        val xMin = AXIS_PADDING_X + marginX
        val xMax = size.width - marginX
        val yMin = marginY
        val yMax = size.height - marginY - AXIS_PADDING_Y

        // X axis data
        val xAxis = xData.map {
            it.toFloat().mapBetween(xValueMin.toFloat(), xValueMax.toFloat(), xMin, xMax)
        }

        // Y axis data
        val yAxis = yData.map {
            it.mapBetween(yValueMax, yValueMin, yMin, yMax)
        }

        assert(yAxis.size == xAxis.size)

        // X axis decoration
        drawLine(
            color = axisDecorationColor,
            start = Offset(AXIS_PADDING_X, size.height - AXIS_PADDING_Y),
            end = Offset(size.width - marginX, size.height - AXIS_PADDING_Y),
            strokeWidth = AXIS_LINE_STROKE_WIDTH
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
                start = Offset(xAxisValue, size.height - AXIS_PADDING_Y - 10f),
                end = Offset(xAxisValue, size.height - AXIS_PADDING_Y + 10f),
                strokeWidth = AXIS_LINE_STROKE_WIDTH
            )
            if (renderDropLines) {
                drawLine(
                    color = axisDecorationColor,
                    start = Offset(xAxisValue, yMin),
                    end = Offset(xAxisValue, size.height - AXIS_PADDING_Y),
                    strokeWidth = AXIS_LINE_STROKE_WIDTH,
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
                    size.height - AXIS_PADDING_Y + 36f,
                    xAxisPaint
                )
            }
        }

        // Y axis decoration
        drawLine(
            color = axisDecorationColor,
            start = Offset(AXIS_PADDING_X, yMin),
            end = Offset(AXIS_PADDING_X, size.height - AXIS_PADDING_Y),
            strokeWidth = AXIS_LINE_STROKE_WIDTH
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
                start = Offset(AXIS_PADDING_X - 10f, yAxisValue),
                end = Offset(AXIS_PADDING_X + 10f, yAxisValue),
                strokeWidth = AXIS_LINE_STROKE_WIDTH
            )
            if (renderGridLines) {
                drawLine(
                    color = axisDecorationColor,
                    start = Offset(AXIS_PADDING_X + 20f, yAxisValue),
                    end = Offset(size.width - marginX, yAxisValue),
                    strokeWidth = AXIS_LINE_STROKE_WIDTH,
                    alpha = 0.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            }

            drawIntoCanvas {
                val yText = when (dataType) {
                    LineChartDataType.TEMPERATURE -> "%.1f%s".format(yCurrent, "°C")
                    LineChartDataType.HUMIDITY -> "%.1f%s".format(yCurrent, "%")
                    LineChartDataType.BATTERY -> "%.2f%s".format(yCurrent, "V")
                }
                it.nativeCanvas.drawText(yText, AXIS_PADDING_X - textSize, yAxisValue, yAxisPaint)
            }
        }

        // Data fill
        val path = Path()
        path.moveTo(xAxis.first(), yMax)
        for (i in xAxis.indices) {
            path.lineTo(xAxis[i], yAxis[i])
        }
        path.lineTo(xAxis.last(), yMax)
        drawPath(
            path = path,
            color = lineColorAnimatable.value,
            alpha = DATA_FILL_ALPHA
        )

        // Data line
        var lastPoint = Offset.Unspecified
        for (i in xAxis.indices) {
            lastPoint = if (i == 0) {
                Offset(xAxis[0], yAxis[0])
            } else {
                val newPoint = Offset(xAxis[i], yAxis[i])
                drawLine(
                    start = lastPoint,
                    end = newPoint,
                    color = lineColorAnimatable.value,
                    strokeWidth = DATA_LINE_STROKE_WIDTH
                )
                newPoint.copy()
            }
        }
    }
}

@Preview("LineChartPreviewLight", widthDp = 400, heightDp = 250, uiMode = UI_MODE_NIGHT_NO)
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
                        1639754902L,
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
                    dataType = LineChartDataType.HUMIDITY,
                    isDarkTheme = false
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
                    ),
                    fromDate = 1639663002L,
                    toDate = 1639749402L,
                    dateType = LineChartDateType.DAY,
                    dataType = LineChartDataType.TEMPERATURE,
                    renderDropLines = true,
                    marginX = 0f,
                    marginY = 0f,
                    isDarkTheme = true
                )
            }
        }
    }
}

@Preview("DateLineChartPreviewDarkEmpty", widthDp = 400, heightDp = 200, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DateLineChartPreviewDarkEmpty() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(250.dp)
            ) {
                DateLineChart(
                    xData = listOf(),
                    yData = listOf(),
                    fromDate = 1639663002L,
                    toDate = 1639749402L,
                    dateType = LineChartDateType.DAY,
                    dataType = LineChartDataType.TEMPERATURE,
                    renderDropLines = true,
                    marginX = 0f,
                    marginY = 0f,
                    isDarkTheme = true
                )
            }
        }
    }
}

