package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Composable
fun DefaultTopAppBar(
    title: String,
    onNavigateBackClicked: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier.height(45.dp),
        backgroundColor = MaterialTheme.colors.primary,
        title = {
            Text(text = title, style = MaterialTheme.typography.h6)
        },
        navigationIcon = if (onNavigateBackClicked != null) {
            {
                IconButton(onClick = { onNavigateBackClicked() }) {
                    Icon(Icons.Filled.ArrowBack, "")
                }
            }
        } else null,
        actions = actions
    )
}

@Preview(
    name = "DefaultTopAppBarPreviewDark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DefaultTopAppBarPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            DefaultTopAppBar("Title")
        }
    }
}

@Preview(
    name = "DefaultTopAppBarPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DefaultTopAppBarPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DefaultTopAppBar(
                title = "Title",
                onNavigateBackClicked = {},
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Filled.Person, "")
                    }
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Filled.MoreVert, "")
                    }
                }
            )
        }
    }
}