package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    heightDp = 150,
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
    heightDp = 150,
    uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "pl"
)
@Composable
fun DeviceItemPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
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
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                ) {
                    Image(
                        alignment = Alignment.Center,
                        painter = painterResource(id = R.drawable.flower3),
                        contentDescription = "",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (isDeviceActive) stringResource(R.string.active).uppercase() else stringResource(R.string.inactive).uppercase(),
                        color = imageActiveIndicatorColor,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Divider(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                        .width(0.5.dp)
                )
            }

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
                        .padding(4.dp, end = 16.dp),
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
//                    Canvas(
//                        modifier = Modifier.weight(1f).padding(end= 12.dp),
//                        onDraw = {
//                            drawCircle(color = imageActiveIndicatorColor, radius = 15f)
//                        }
//                    )
                }
                Divider()
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.birthday) + ": ",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = device.birthday.toFormattedString(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider()
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.last_update) + ": ",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = device.lastUpdate.toFormattedString(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider()
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.next_watering) + ": ",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = device.nextWatering.toFormattedString(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
