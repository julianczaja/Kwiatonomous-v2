package com.corrot.kwiatonomousapp.presentation.login

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.login.LoginScreenViewModel.Event.LOGGED_IN
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import kotlinx.coroutines.runBlocking


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                LOGGED_IN -> {
                    navController.popBackStack()
                    navController.navigate(Screen.Devices.route)
                }
            }
        }
    }

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
            label = { Text("Login") },
            modifier = Modifier.padding(4.dp)
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.passwordChanged(it) },
            label = { Text("Password") },
            modifier = Modifier.padding(4.dp)
        )

        Button(
            onClick = {
                runBlocking {
                    viewModel.loginClicked()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Login")
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
    "LoginScreenPreviewLight",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LoginScreenPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            LoginScreen(navController = rememberNavController())
        }
    }
}