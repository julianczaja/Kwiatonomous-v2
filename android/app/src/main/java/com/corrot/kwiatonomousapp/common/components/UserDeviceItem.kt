package com.corrot.kwiatonomousapp.common.components

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime


@Composable
fun UserDeviceItem(
    userDevice: UserDevice,
    lastDeviceUpdate: DeviceUpdate?,
    onItemClick: ((UserDevice) -> Unit)? = null,
) {
    BoxWithConstraints {
        val maxWidth = maxWidth
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onItemClick != null) { onItemClick?.let { it(userDevice) } }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = userDevice.deviceName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(8.dp)
                )
                val imageId = getDrawableIdByName(LocalContext.current, userDevice.deviceImageName)
                Image(
                    alignment = Alignment.Center,
                    painter = painterResource(imageId),
                    contentDescription = "",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(horizontal = 8.dp)
                )
                if (lastDeviceUpdate != null) {
                    Divider(
                        color = MaterialTheme.colors.primaryVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    LastDeviceUpdate(lastDeviceUpdate, maxWidth > 300.dp)
                }
            }
        }
    }
}

@Composable
fun LastDeviceUpdate(
    lastDeviceUpdate: DeviceUpdate,
    isHorizontal: Boolean
) {
    if (isHorizontal) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Temperature(temperature = lastDeviceUpdate.temperature)
            Humidity(humidity = lastDeviceUpdate.humidity)
            BatteryLevel(batteryLevel = lastDeviceUpdate.batteryLevel)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Temperature(temperature = lastDeviceUpdate.temperature)
            Humidity(humidity = lastDeviceUpdate.humidity)
            BatteryLevel(batteryLevel = lastDeviceUpdate.batteryLevel)
        }
    }
}

private fun getDrawableIdByName(context: Context, drawableName: String) =
    context.resources.getIdentifier(drawableName, "drawable", context.packageName)


@Preview(
    "UserDeviceItemPreviewLight",
    widthDp = 400,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun UserDeviceItemPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            UserDeviceItem(
                userDevice = UserDevice(
                    "testID",
                    "My pot flower",
                    "flower_2",
                ),
                lastDeviceUpdate = DeviceUpdate(
                    "testID",
                    LocalDateTime.now(),
                    68,
                    3.92f,
                    23.2f,
                    57.4f
                ),
                onItemClick = {})
        }
    }
}

@Preview(
    "UserDeviceItemPreviewDark",
    widthDp = 200,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "pl"
)
@Composable
fun UserDeviceItemPreviewDark() {
    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            UserDeviceItem(
                userDevice = UserDevice(
                    "testID",
                    "My flower with long name", // max 24 characters
                    "flower_1",
                ),
                lastDeviceUpdate = null,
                onItemClick = {})
        }
    }
}