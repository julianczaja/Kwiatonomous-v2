package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.Constants.DEVICE_INACTIVE_TIME_SECONDS
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.presentation.theme.DeviceActiveColor
import com.corrot.kwiatonomousapp.presentation.theme.DeviceInActiveColor
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime

@Preview(
    "DeviceItemPreviewLight",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DeviceItemPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DeviceItem(
                device = Device(
                    "testID",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
                ),
                onItemClick = {})
        }
    }
}

@Preview(
    "DeviceItemPreviewDark",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "pl"
)
@Composable
fun DeviceItemPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            DeviceItem(
                device = Device(
                    "testID",
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now().minusDays(2),
                ),
                onItemClick = {})
        }
    }
}

@Composable
fun DeviceItem(
    device: Device,
    onItemClick: ((Device) -> Unit)? = null
) {
    val isDeviceActive =
        (LocalDateTime.now().toLong() - (device.lastUpdate.toLong())) < DEVICE_INACTIVE_TIME_SECONDS
    val imageActiveIndicatorColor = if (isDeviceActive) DeviceActiveColor else DeviceInActiveColor

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(enabled = onItemClick != null) { onItemClick?.let { it(device) } }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .padding(4.dp)
                ) {
                    Canvas(
                        modifier = Modifier.padding(start = 0.dp),
                        onDraw = {
                            drawCircle(color = imageActiveIndicatorColor, radius = 12f)
                        }
                    )
                }
                Text(
                    text = if (isDeviceActive)
                        stringResource(R.string.active).uppercase()
                    else
                        stringResource(R.string.inactive).uppercase(),
                    color = imageActiveIndicatorColor,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                )
            }
            Divider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.id) + ": ",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = device.deviceId,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.birthday) + ": ",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = device.birthday.toFormattedString(),
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.last_update) + ": ",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = device.lastUpdate.toFormattedString(),
                    style = MaterialTheme.typography.body2,
                )
            }
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.next_watering) + ": ",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    text = device.nextWatering.toFormattedString(),
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}
