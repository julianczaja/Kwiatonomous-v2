package com.corrot.kwiatonomousapp

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberKwiatonomousAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(snackbarHostState = remember { SnackbarHostState() }),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
) = remember {
    KwiatonomousAppState(
        scaffoldState = scaffoldState,
        snackbarScope = snackbarScope,
        navController = navController
    )
}

class KwiatonomousAppState(
    val scaffoldState: ScaffoldState,
    val snackbarScope: CoroutineScope,
    val navController: NavHostController,
) {
    fun showSnackbar(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        snackbarScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message = message, duration = duration)
        }
    }
}