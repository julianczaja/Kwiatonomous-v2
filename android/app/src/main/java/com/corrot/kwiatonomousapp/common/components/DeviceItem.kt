package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.Constants.DEVICE_INACTIVE_TIME_SECONDS
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.common.toLong
import com.corrot.kwiatonomousapp.domain.model.Device
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
    uiMode = Configuration.UI_MODE_NIGHT_YES
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
    val imageActiveIndicatorColor = if (isDeviceActive) Color.Green else Color.Red
    val imageBackgroundColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onItemClick != null) { onItemClick?.let { it(device) } }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                Canvas(modifier = Modifier, onDraw = {
                    drawCircle(color = imageActiveIndicatorColor, radius = 15f)
                })
            }
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .background(imageBackgroundColor)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flower1),
                        contentDescription = "",
                        Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "ID: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                        Text(
                            text = device.id,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Birthday: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                        Text(
                            text = device.birthday.toFormattedString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Last update: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                        Text(
                            text = device.lastUpdate.toFormattedString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Next watering: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1,
                        )
                        Text(
                            text = device.nextWatering.toFormattedString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }
}