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
fun Humidity(humidity: Float) {
    Row(
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.Start,
        modifier = GlanceModifier
            .padding(start = 8.dp)
            .fillMaxWidth()
    ) {
        Image(
            provider = ImageProvider(R.drawable.humidity),
            contentDescription = null,
            modifier = GlanceModifier.size(24.dp)
        )
        Text(
            text = LocalContext.current.getString(R.string.humidity_format).format(humidity),
            modifier = GlanceModifier.padding(start = 8.dp)
        )
    }
}