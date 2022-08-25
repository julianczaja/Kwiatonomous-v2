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


@Composable
fun TimePicker(
    title: String,
    initialValue: LocalTime?,
    onDismiss: () -> Unit,
    onConfirmClick: (LocalTime) -> Unit,
) {
    var hours: Int? by rememberSaveable { mutableStateOf(initialValue?.hour ?: 0) }
    var minutes: Int? by rememberSaveable { mutableStateOf(initialValue?.minute ?: 0) }

    fun isHourValid(hours: Int?) = (hours != null) && (hours in 0..23)
    fun isMinuteValid(minutes: Int?) = (minutes != null) && (minutes in 0..59)

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
            ) {

                // Title
                Row(
                    Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6
                    )
                }

                // Hour/minute picking
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        NumberInputField(
                            value = if (hours != null) hours.toString() else "",
                            onValueChange = {
                                hours = try {
                                    it.toInt().coerceIn(0, 23)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            isError = !isHourValid(hours)
                        )
                        Text(
                            text = stringResource(R.string.hour),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.height(75.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = ":",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        )
                    }
                    Column {
                        NumberInputField(
                            value = if (minutes != null) minutes.toString() else "",
                            onValueChange = {
                                minutes = try {
                                    it.toInt().coerceIn(0, 59)
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            isError = !isMinuteValid(minutes)
                        )
                        Text(
                            text = stringResource(R.string.minute),
                            style = MaterialTheme.typography.overline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                CancelOkButtons(
                    isOkEnabled = isHourValid(hours) && isMinuteValid(minutes),
                    onCancelClicked = onDismiss,
                    onOkClicked = {
                        if (isHourValid(hours) && isMinuteValid(minutes)) {
                            onConfirmClick(LocalTime.of(hours!!, minutes!!))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = MaterialTheme.typography.h4.copy(textAlign = TextAlign.Center),
        onValueChange = onValueChange,
        modifier = Modifier.width(100.dp)
    )
}

@Composable
private fun CancelOkButtons(
    isOkEnabled: Boolean = true,
    onCancelClicked: () -> Unit,
    onOkClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onCancelClicked
        ) {
            Text(
                text = stringResource(R.string.cancel).uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
            )
        }
        TextButton(
            enabled = isOkEnabled,
            onClick = onOkClicked
        ) {
            Text(
                text = stringResource(R.string.ok).uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
            )
        }
    }
}

@Preview(
    "TimePickerPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    heightDp = 300
)
@Composable
fun TimePickerPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            TimePicker(
                title = "Enter watering time",
                initialValue = null,
                onDismiss = {},
                onConfirmClick = {}
            )
        }
    }
}

@Preview(
    "TimePickerPreviewDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 300
)
@Composable
fun TimePickerPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            TimePicker(
                title = "Enter notifications time",
                initialValue = null,
                onDismiss = {},
                onConfirmClick = {}
            )
        }
    }
}

