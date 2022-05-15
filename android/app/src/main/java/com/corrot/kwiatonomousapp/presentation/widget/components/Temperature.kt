package com.corrot.kwiatonomousapp.presentation.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.layout.*
import androidx.glance.text.Text
import com.corrot.kwiatonomousapp.R

@Composable
fun Temperature(temperature: Float) {
    Row(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.Start,
        modifier = GlanceModifier
            .padding(start = 8.dp)
            .fillMaxWidth()
    ) {
        Image(
            provider = ImageProvider(R.drawable.temperature),
            contentDescription = null,
            modifier = GlanceModifier.size(24.dp)
        )
        Text(
            text = LocalContext.current.getString(R.string.temperature_format).format(temperature),
            modifier = GlanceModifier.padding(start = 8.dp)
        )
    }
}
