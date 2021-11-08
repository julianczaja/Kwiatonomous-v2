package com.corrot.kwiatonomousapp.presentation.splashscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout


@Composable
fun SplashScreen(navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Display splash screen for 1.5s or until screen tapped
        try {
            withTimeout(1500L) { while (!isClicked) delay(50) }
        } finally {
            navController.popBackStack()
            navController.navigate(Screen.Devices.route)
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .clickable { isClicked = true }
    ) {
        Image(
            painter = painterResource(id = R.drawable.flower3),
            contentDescription = "",
            modifier = Modifier.scale(scale.value)
        )
        Text(
            text = "Kwiatonomous",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(top = 64.dp)
        )
    }
}