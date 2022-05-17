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

@Preview("HumidityPreviewLight", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HumidityPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            Humidity(humidity = 65.12f)
        }
    }
}

@Preview("HumidityPreviewDark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HumidityPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            Humidity(humidity = 65.12f)
        }
    }
}

@Composable
fun Humidity(humidity: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.humidity),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.humidity_format).format(humidity),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}