package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import com.corrot.kwiatonomousapp.presentation.theme.WarningColor

@Composable
fun WarningBox(
    message: String,
    onCancelClicked: () -> Unit,
    onConfirmClicked: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancelClicked
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningColor)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_warning),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                    )
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onCancelClicked() }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel).uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
                        )
                    }
                    TextButton(
                        onClick = onConfirmClicked
                    ) {
                        Text(
                            text = stringResource(R.string.yes).uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    "WarningBoxPreviewLight",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun WarningBoxPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            WarningBox("Message, message, message, message, message, message?", {}, {})
        }
    }
}

@Preview(
    "WarningBoxPreviewDark",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun WarningBoxPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            WarningBox("Message, message, message, message, message, message?", {}, {})
        }
    }
}
