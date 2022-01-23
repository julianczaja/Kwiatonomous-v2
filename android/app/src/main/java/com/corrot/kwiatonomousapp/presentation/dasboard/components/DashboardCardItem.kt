package com.corrot.kwiatonomousapp.presentation.dasboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@ExperimentalMaterialApi
@Composable
fun DashboardCardItem(text: String, onClicked: () -> Unit) {
    Card(
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface,
        onClick = onClicked,
        modifier = Modifier
            .padding(8.dp)
            .height(150.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}