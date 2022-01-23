package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Preview(
    "ErrorBoxCancelRetryPreviewLight",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ErrorBoxCancelRetryPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ErrorBoxCancelRetry("Error box message", {}, {})
            }
        }
    }
}

@Preview(
    "ErrorBoxCancelRetryPreviewDark",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ErrorBoxCancelRetryPreviewDark() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ErrorBoxCancelRetry("Error box message", {}, {})
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
                        text = "Error".uppercase(),
                        textAlign = TextAlign.Center,
                        fontSize = 19.sp,
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
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onCancel() }
                    ) {
                        Text(
                            text = "Cancel".uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                        )
                    }
                    TextButton(
                        onClick = onRetry
                    ) {
                        Text(
                            text = "Retry".uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.overline.copy(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}