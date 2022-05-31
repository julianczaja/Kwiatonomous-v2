package com.corrot.kwiatonomousapp.presentation.splashscreen

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.KwiatonomousAppState
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.ErrorBoxCancel
import com.corrot.kwiatonomousapp.presentation.Screen


@Composable
fun SplashScreen(
    kwiatonomousAppState: KwiatonomousAppState,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(true) {
        viewModel.checkIfLoggedIn()
        viewModel.eventFlow.collect { event ->
            when (event) {
                SplashScreenViewModel.Event.NOT_LOGGED_IN -> {
                    kwiatonomousAppState.navController.popBackStack()
                    kwiatonomousAppState.navController.navigate(Screen.Login.route)
                }
                SplashScreenViewModel.Event.LOGGED_IN -> {
                    kwiatonomousAppState.showSnackbar("Logged in")
                    kwiatonomousAppState.navController.popBackStack()
                    kwiatonomousAppState.navController.navigate(Screen.Dashboard.route)
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.flower_2),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(top = 64.dp)
        )
    }

    viewModel.error.value?.let { error ->
        val activity = (LocalContext.current as? Activity)
        ErrorBoxCancel(
            message = error,
            onCancel = { activity?.finish() },
        )
    }
}