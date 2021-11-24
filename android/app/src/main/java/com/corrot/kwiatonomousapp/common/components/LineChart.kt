package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.common.mapBetween
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Preview("Light", widthDp = 400, heightDp = 250, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun LineChartPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(250.dp)
            ) {
                lineChart(
                    xData = listOf(1f, 2f, 3f, 4f, 10f, 15f),
                    yData = listOf(10f, 12f, 13f, 14f, 10f, 15f),
                    title = "Humidity",
                )
            }
        }
    }
}

@Preview("DARK", widthDp = 400, heightDp = 200, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LineChartPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(250.dp)
            ) {
                lineChart(
                    xData = listOf(1f, 2f, 3f, 4f, 10f, 15f),
                    yData = listOf(10f, 12f, 13f, 14f, 10f, 15f),
                    title = "Temperature",
                    yAxisUnit = "°C",
                    renderDropLines = true,
                    marginX = 0f,
                    marginY = 0f
                )
            }
        }
    }
}

@Composable
fun lineChart(
    xData: List<Float>,
    yData: List<Float>,
    title: String = "",
    marginX: Float = 50f,
    marginY: Float = 50f,
    lineColor: Color = MaterialTheme.colors.primary,
    circleColor: Color = MaterialTheme.colors.primary,
    xAxisUnit: String = "",
    yAxisUnit: String = "",
    xAxisDividersCount: Int = 5,
    yAxisDividersCount: Int = 4,
    renderGridLines: Boolean = true,
    renderDropLines: Boolean = false,
) {
    val isDarkTheme = isSystemInDarkTheme()
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

    val xValueMax = xData.maxOrNull() ?: 0f
    val xValueMin = xData.minOrNull() ?: 0f
    val yValueMax = yData.maxOrNull() ?: 0f
    val yValueMin = yData.minOrNull() ?: 0f

    val axisPaddingX = 100f
    val axisPaddingY = 50f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(
                border = BorderStroke(0.5.dp, Color.White),
                shape = RectangleShape
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
        )
        Canvas(Modifier.fillMaxSize()) {
            val xMin = axisPaddingX + marginX
            val xMax = size.width - marginX
            val yMin = marginY
            val yMax = size.height - marginY - axisPaddingY

            // X axis data
            val xAxis = xData.map {
                it.mapBetween(
                    xValueMin, xValueMax,
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
                val xAxisValue = xCurrent.mapBetween(
                    xValueMin, xValueMax,
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
                    it.nativeCanvas.drawText(
                        "%.1f%s".format(xCurrent, xAxisUnit),
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
                val yCurrent = yValueMin + (yAxisValueStep * i)
                val yAxisValue = yCurrent.mapBetween(
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

            // Data line
            var lastPoint = Offset.Unspecified
            for (i in xAxis.indices) {
                if (i == 0) {
                    lastPoint = Offset(xAxis[0], yAxis[0])
                    drawCircle(
                        color = circleColor,
                        radius = 4f,
                        center = lastPoint
                    )
                } else {
                    val newPoint = Offset(xAxis[i], yAxis[i])
                    drawLine(
                        start = lastPoint,
                        end = newPoint,
                        color = lineColor,
                        strokeWidth = 2f
                    )
                    drawCircle(
                        color = circleColor,
                        radius = 4f,
                        center = newPoint
                    )
                    lastPoint = newPoint.copy()
                }
            }
        }
    }
}