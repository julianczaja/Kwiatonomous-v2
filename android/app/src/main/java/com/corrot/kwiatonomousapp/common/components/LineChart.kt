package com.corrot.kwiatonomousapp.common.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.common.mapBetween

@Composable
fun lineChart(
    xData: List<Float>,
    yData: List<Float>,
    title: String,
    marginX: Float = 50f,
    marginY: Float = 50f,
    lineColor: Color = MaterialTheme.colors.primary,
    circleColor: Color = MaterialTheme.colors.primary,
    yAxisUnit: String = "",
    yAxisDividersCount: Int = 4,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .border(
//                border = BorderStroke(0.5.dp, Color.White),
//                shape = RectangleShape
//            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
        )
        Canvas(Modifier.fillMaxSize()) {
            val xMin = 100f + marginX
            val xMax = size.width - marginX
            val yMin = marginY
            val yMax = size.height - marginY

            // X axis data
            val xValueMax = xData.maxOrNull() ?: 0f
            val xValueMin = xData.minOrNull() ?: 0f
            val xAxis = xData.map {
                it.mapBetween(
                    xValueMin, xValueMax,
                    xMin, xMax
                )
            }

            // Y axis data
            val yValueMax = yData.maxOrNull() ?: 0f
            val yValueMin = yData.minOrNull() ?: 0f
            val yAxis = yData.map {
                it.mapBetween(
                    yValueMax, yValueMin,
                    yMin, yMax
                )
            }
            assert(yAxis.size == xAxis.size)

            // X axis decoration
            drawLine(
                color = Color.White,
                start = Offset(100f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )

            // Y axis decoration
            drawLine(
                color = Color.White,
                start = Offset(100f, 0f),
                end = Offset(100f, size.height),
                strokeWidth = 2f
            )
            val yAxisValueStep = (yValueMax - yValueMin) / yAxisDividersCount
            val paint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                textSize = 24f
                textAlign = Paint.Align.RIGHT
            }
            for (i in 0..yAxisDividersCount) {
                val yCurrent = yValueMin + (yAxisValueStep * i)
                val yAxisValue = yCurrent.mapBetween(
                    yValueMax, yValueMin,
                    yMin, yMax
                )
                drawLine(
                    color = Color.White,
                    start = Offset(90f, yAxisValue),
                    end = Offset(110f, yAxisValue),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.White,
                    start = Offset(110f, yAxisValue),
                    end = Offset(size.width, yAxisValue),
                    strokeWidth = 2f,
                    alpha = 0.3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        "%.1f%s".format(yCurrent, yAxisUnit),
                        80f,
                        yAxisValue - 2f,
                        paint
                    )
                }
            }

            // Data line
            var lastPoint = Offset.Unspecified
            for (i in xAxis.indices) {
                if (i == 0) {
                    lastPoint = Offset(xAxis[0], yAxis[0])
//                    drawCircle(
//                        color = circleColor,
//                        radius = 4f,
//                        center = lastPoint
//                    )
                } else {
                    val newPoint = Offset(xAxis[i], yAxis[i])
                    drawLine(
                        start = lastPoint,
                        end = newPoint,
                        color = lineColor,
                        strokeWidth = 2f
                    )
//                    drawCircle(
//                        color = circleColor,
//                        radius = 4f,
//                        center = newPoint
//                    )
                    lastPoint = newPoint.copy()
                }
            }
        }
    }
}