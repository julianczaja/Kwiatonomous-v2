package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime


@Composable
fun AddWateringEventDialog(
    dateTime: LocalDateTime,
    onDateTimeChange: (LocalDateTime) -> Unit, // TODO: Implement date and time picker
    onCancelClicked: () -> Unit,
    onAddClicked: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancelClicked,
    ) {
        Card(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = dateTime.toFormattedString(),
                    modifier = Modifier.padding(4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCancelClicked,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = onAddClicked,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}

@Preview(
    "AddWateringEventDialogPreviewLight",
    widthDp = 400,
    heightDp = 700,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddWateringEventDialogPreviewLight() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            AddWateringEventDialog(
                LocalDateTime.now(), {}, {}, {}
            )
        }
    }
}
