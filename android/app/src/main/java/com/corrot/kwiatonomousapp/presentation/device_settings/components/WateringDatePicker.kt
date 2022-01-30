package com.corrot.kwiatonomousapp.presentation.device_settings.components

import android.widget.CalendarView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun WateringDatePicker(
    initialValue: LocalDateTime?,
    onDismiss: () -> Unit,
    onConfirmClick: (LocalDateTime) -> Unit
) {
    var year: Int by rememberSaveable { mutableStateOf(initialValue?.year ?: 0) }
    var month: Int by rememberSaveable { mutableStateOf(initialValue?.monthValue ?: 0) }
    var dayOfMonth: Int by rememberSaveable { mutableStateOf(initialValue?.dayOfMonth ?: 0) }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ) {
            Column {
                AndroidView(
                    factory = { CalendarView(it) },
                    update = { views ->
                        views.minDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000
                        views.setOnDateChangeListener { _, _year, _month, _dayOfMonth ->
                            year = _year
                            month = _month + 1
                            dayOfMonth = _dayOfMonth
                        }
                        initialValue?.let { views.date = it.toEpochSecond(ZoneOffset.UTC) * 1000 }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel).uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                        )
                    }
                    TextButton(
                        onClick = {
                            onConfirmClick(LocalDateTime.of(year, month, dayOfMonth, 0, 0))
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.ok).uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}