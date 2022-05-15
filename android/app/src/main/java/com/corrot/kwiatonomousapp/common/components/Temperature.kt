package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Preview("TemperaturePreviewLight", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TemperaturePreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Temperature(temperature = 25.12f)
        }
    }
}

@Preview("TemperaturePreviewDark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TemperaturePreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Temperature(temperature = 25.12f)
        }
    }
}

@Composable
fun Temperature(temperature: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(start = 8.dp)
            .fillMaxWidth()
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.temperature),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.temperature_format).format(temperature),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}