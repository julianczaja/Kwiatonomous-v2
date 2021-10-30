package com.corrot.kwiatonomousapp.presentation.dasboard

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corrot.kwiatonomousapp.presentation.dasboard.components.DeviceItem
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "Device",
            textAlign = TextAlign.Center,
            style= MaterialTheme.typography.h1,
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = 12.dp)
        ) {
            with(state) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                if (device != null) {
                    DeviceItem(device = device, onItemClick = {
                        Log.i("DashboardScreen", "Device clicked!")
                    })
                }
                if (!error.isNullOrBlank()) {
                    Text(
                        text = error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

        }
    }
}