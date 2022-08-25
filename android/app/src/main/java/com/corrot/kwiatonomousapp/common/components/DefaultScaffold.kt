package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable


@Composable
fun DefaultScaffold(
    scaffoldState: ScaffoldState,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(
                hostState = scaffoldState.snackbarHostState,
                snackbar = { DefaultSnackbar(snackbarData = it) }
            )
        }
    ) { padding ->
        content(padding)
    }
}