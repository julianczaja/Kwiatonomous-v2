package com.corrot.kwiatonomousapp.presentation.register

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.register.RegisterScreenViewModel.Event.REGISTERED
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import kotlinx.coroutines.runBlocking


@Composable
fun RegisterScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: RegisterScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                REGISTERED -> {
                    // FIXME: Make sure that previous screen is LoginScreen
                    kwiatonomousAppState.navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.register),
                onNavigateBackClicked = {
                    kwiatonomousAppState.navController.popBackStack()
                }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.flower_1),
                contentDescription = "",
                modifier = Modifier
                    .size(150.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(top = 16.dp)
            )
            Divider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 32.dp))


            OutlinedTextField(
                value = state.login,
                onValueChange = { viewModel.loginChanged(it) },
                label = { Text(stringResource(R.string.login_noun)) },
                modifier = Modifier.padding(4.dp)
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.passwordChanged(it) },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.padding(4.dp)
            )

            Button(
                onClick = {
                    runBlocking {
                        viewModel.registerClicked()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(R.string.register))
            }

        }
    }

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
    state.error?.let {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            ErrorBoxCancel(
                message = state.error,
                onCancel = { viewModel.confirmError() }
            )
        }
    }
}


@Preview(
    "RegisterScreenPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun RegisterScreenPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            RegisterScreen(
                KwiatonomousAppState(
                    navController = rememberNavController(),
                    scaffoldState = rememberScaffoldState(),
                    snackbarScope = rememberCoroutineScope()
                )
            )
        }
    }
}