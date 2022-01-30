package com.corrot.kwiatonomousapp.presentation.device_settings.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalTime


@Preview(
    "WateringTimePickerPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun WateringTimePickerPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            WateringTimePicker(initialValue = null, onDismiss = {}, onConfirmClick = {})
        }
    }
}

private fun isHourValid(hours: Int?) = (hours != null) && (hours in 0..23)

private fun isMinuteValid(minutes: Int?) = (minutes != null) && (minutes in 0..59)

@Composable
fun WateringTimePicker(
    initialValue: LocalTime?,
    onDismiss: () -> Unit,
    onConfirmClick: (Pair<Int, Int>) -> Unit
) {
    var hours: Int? by rememberSaveable { mutableStateOf(initialValue?.hour ?: 0) }
    var minutes: Int? by rememberSaveable { mutableStateOf(initialValue?.minute ?: 0) }


    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    .fillMaxSize()
            ) {

                // Title
                Row(
                    Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.enter_watering_time).uppercase(),
                        style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                    )
                }

                // Hour/minute picking
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .width(100.dp)
                    ) {
                        // TODO: separate from this composable
                        OutlinedTextField(
                            value = if (hours != null) hours.toString() else "",
                            isError = !isHourValid(hours),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.h1.copy(textAlign = TextAlign.Center),
                            onValueChange = {
                                hours = try {
                                    it.toInt().coerceIn(0, 23)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            modifier = Modifier.height(75.dp)
                        )
                        Text(
                            text = stringResource(R.string.hour),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.height(75.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = ":",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h1,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .width(100.dp)
                    ) {
                        OutlinedTextField(
                            value = if (minutes != null) minutes.toString() else "",
                            isError = !isMinuteValid(minutes),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.h1.copy(textAlign = TextAlign.Center),
                            onValueChange = {
                                minutes = try {
                                    it.toInt().coerceIn(0, 59)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            modifier = Modifier.height(75.dp)
                        )
                        Text(
                            text = stringResource(R.string.minute),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // 'Cancel' and 'Ok' Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel).uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                        )
                    }
                    TextButton(
                        enabled = isHourValid(hours) && isMinuteValid(minutes),
                        onClick = {
                            // Validation
                            if (isHourValid(hours) && isMinuteValid(minutes)) {
                                onConfirmClick(Pair(hours!!, minutes!!))
                            }
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
