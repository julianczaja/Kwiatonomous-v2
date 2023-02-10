package com.corrot.kwiatonomousapp.presentation.login

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.DefaultScaffold
import com.corrot.kwiatonomousapp.common.components.DefaultTopAppBar
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.domain.model.KwiatonomousAppState
import com.corrot.kwiatonomousapp.presentation.Screen
import com.corrot.kwiatonomousapp.presentation.login.LoginScreenViewModel.Event.LOGGED_IN
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Composable
fun LoginScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: LoginScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                LOGGED_IN -> {
                    kwiatonomousAppState.showSnackbar(context.getString(R.string.logged_in))
                    kwiatonomousAppState.navController.popBackStack()
                    kwiatonomousAppState.navController.navigate(Screen.Dashboard.route)
                }
            }
        }
    }

    DefaultScaffold(
        scaffoldState = kwiatonomousAppState.scaffoldState,
        topBar = { DefaultTopAppBar(title = stringResource(R.string.login_verb)) },
    ) { padding ->
        LoginScreenContent(
            login = state.login,
            password = state.password,
            padding = padding,
            onLoginChanged = { viewModel.loginChanged(it) },
            onPasswordChanged = { viewModel.passwordChanged(it) },
            onLoginClicked = { viewModel.loginClicked() },
            onRegisterClicked = { kwiatonomousAppState.navController.navigate(Screen.Register.route) }
        )
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
                    message = state.error ?: "Unknown error",
                    onCancel = { viewModel.confirmError() }
                )
            }
        }
    }
}


@Composable
private fun LoginScreenContent(
    login: String,
    password: String,
    padding: PaddingValues,
    onLoginChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

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
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(top = 16.dp)
        )
        Divider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 32.dp))


        OutlinedTextField(
            value = login,
            onValueChange = {
                if (it.endsWith("\n")) {
                    focusManager.moveFocus(FocusDirection.Next)
                } else {
                    onLoginChanged(it)
                }
            },
            label = { Text(stringResource(R.string.login_noun)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier.padding(4.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                if (it.endsWith("\n")) {
                    onLoginClicked()
                } else {
                    onPasswordChanged(it)
                }
            },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.padding(4.dp)
        )

        Button(
            onClick = onLoginClicked,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.log_in))
        }
        TextButton(
            onClick = onRegisterClicked,
        ) {
            Text(
                text = stringResource(id = R.string.register_prompt),
                color = Color.Gray,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
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
            LoginScreenContent(
                "login", "password", PaddingValues(), {}, {}, {}, {}
            )
        }
    }
}