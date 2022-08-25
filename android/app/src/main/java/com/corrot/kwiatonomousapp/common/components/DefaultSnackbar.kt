package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Composable
fun DefaultSnackbar(snackbarData: SnackbarData) {
    DefaultSnackbarContent(snackbarData.message)
}

@Composable
private fun DefaultSnackbarContent(message: String) {
    Box(Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(2.dp),
            elevation = 6.dp,
            backgroundColor = Color.DarkGray,
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    style = MaterialTheme.typography.body1,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(
    "DefaultSnackbarPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    heightDp = 300
)
@Composable
fun DefaultSnackbarPreviewLight(
) {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DefaultSnackbarContent(message = "Example long long long long long long long long long long long long long long long long long long long long long long long long long long long long long message")
        }
    }
}

@Preview(
    "DefaultSnackbarPreviewDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    heightDp = 300
)
@Composable
fun DefaultSnackbarPreviewDark(
) {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            DefaultSnackbarContent(message = "Message")
        }
    }
}
