package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

// TODO: Unify content of error boxes
//       (https://stackoverflow.com/questions/65258997/how-to-pass-children-in-jetpack-compose-to-a-custom-composable)

@Composable
fun ErrorBoxCancel(
    message: String,
    onCancel: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel
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
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.error)
                ) {
                    Text(
                        text = stringResource(R.string.error).uppercase(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onError,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                CancelButton(onCancel)
            }
        }
    }
}

@Composable
fun ErrorBoxCancelRetry(
    message: String,
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel
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
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.error)
                ) {
                    Text(
                        text = stringResource(R.string.error).uppercase(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onError,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                CancelRetryButtons(onCancel, onRetry)
            }
        }
    }
}

@Composable
private fun CancelRetryButtons(
    onCancel: () -> Unit,
    onRetry: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onCancel() }
        ) {
            Text(
                text = stringResource(R.string.cancel).uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
            )
        }
        TextButton(
            onClick = onRetry
        ) {
            Text(
                text = stringResource(R.string.retry).uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
            )
        }
    }
}

@Composable
private fun CancelButton(
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onCancel() }
        ) {
            Text(
                text = stringResource(R.string.cancel).uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.button.copy(fontSize = 12.sp)
            )
        }
    }
}

@Preview(
    "ErrorBoxCancelRetryPreviewLight",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    heightDp = 150
)
@Composable
fun ErrorBoxCancelRetryPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ErrorBoxCancelRetry(
                    "Error box message, error box message, error box message.",
                    {},
                    {})
            }
        }
    }
}

@Preview(
    "ErrorBoxCancelRetryPreviewDark",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 150
)
@Composable
fun ErrorBoxCancelRetryPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ErrorBoxCancelRetry(
                    "Error box message, error box message, error box message.",
                    {},
                    {})
            }
        }
    }
}

@Preview(
    "ErrorBoxCancelPreviewDark",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 150
)
@Composable
fun ErrorBoxCancelPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ErrorBoxCancel(message = "Error box message, error box message, error box message.", onCancel = {})
            }
        }
    }
}