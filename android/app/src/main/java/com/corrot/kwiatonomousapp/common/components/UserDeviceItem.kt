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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalDateTime

@Preview(
    "UserDeviceItemPreviewLight",
    widthDp = 200,
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
                    R.drawable.flower_2,
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
    heightDp = 250,
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
                    R.drawable.flower_1,
                ),
                lastDeviceUpdate = null,
                onItemClick = {})
        }
    }
}

@Composable
fun UserDeviceItem(
    userDevice: UserDevice,
    lastDeviceUpdate: DeviceUpdate?,
    onItemClick: ((UserDevice) -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(enabled = onItemClick != null) { onItemClick?.let { it(userDevice) } }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = userDevice.deviceName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
            Image(
                alignment = Alignment.Center,
                painter = painterResource(userDevice.deviceImageId),
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
                LastDeviceUpdate(lastDeviceUpdate)
            }
        }
    }
}

@Composable
fun LastDeviceUpdate(
    lastDeviceUpdate: DeviceUpdate
) {
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
            text = stringResource(id = R.string.temperature_format).format(lastDeviceUpdate.temperature),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(start = 8.dp, top = 6.dp, bottom = 4.dp)
            .fillMaxWidth()
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(id = R.drawable.humidity),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.humidity_format).format(lastDeviceUpdate.humidity),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}