package com.corrot.kwiatonomousapp.presentation.register

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.presentation.register.RegisterScreenViewModel.Event.REGISTERED
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import kotlinx.coroutines.runBlocking


@Composable
fun RegisterScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: RegisterScreenViewModel = hiltViewModel(),
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
    ) { padding ->
        RegisterScreenContent(
            userName = state.userName,
            login = state.login,
            password = state.password,
            padding = padding,
            onUserNameChanged = { viewModel.userNameChanged(it) },
            onLoginChanged = { viewModel.loginChanged(it) },
            onPasswordChanged = { viewModel.passwordChanged(it) },
            onRegisterClicked = { viewModel.registerClicked() }
        )
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

@Composable
private fun RegisterScreenContent(
    userName: String,
    login: String,
    password: String,
    padding: PaddingValues,
    onUserNameChanged: (String) -> Unit,
    onLoginChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_icon_v1),
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
            value = userName,
            onValueChange = { onUserNameChanged(it) },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.padding(4.dp)
        )
        OutlinedTextField(
            value = login,
            onValueChange = { onLoginChanged(it) },
            label = { Text(stringResource(R.string.login_noun)) },
            modifier = Modifier.padding(4.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChanged(it) },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.padding(4.dp)
        )

        Button(
            onClick = {
                runBlocking {
                    onRegisterClicked()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.register))
        }

    }
}


@Preview(
    "RegisterScreenContentPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun RegisterScreenContentPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            RegisterScreenContent(
                "user name", "login", "password", PaddingValues(), {}, {}, {}, {}
            )
        }
    }
}