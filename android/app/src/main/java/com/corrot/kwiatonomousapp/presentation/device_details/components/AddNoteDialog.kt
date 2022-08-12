package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Composable
fun AddNoteDialog(
    title: String,
    content: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
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
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(id = R.string.title)) },
                    modifier = Modifier.padding(4.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    label = { Text(stringResource(id = R.string.content)) },
                    modifier = Modifier
                        .padding(4.dp)
                        .height(150.dp)
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
                        enabled = title.isNotEmpty(),
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
    "AddNoteDialogPreviewLight",
    widthDp = 400,
    heightDp = 700,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddNoteDialogPreviewLight() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            AddNoteDialog(
                "", "", {}, {}, {}, {}
            )
        }
    }
}
