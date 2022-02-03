package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme

@Preview(
    "UserDeviceItemPreviewLight",
    widthDp = 200,
    heightDp = 250,
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
                onItemClick = {})
        }
    }
}

@Composable
fun UserDeviceItem(
    userDevice: UserDevice,
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
            Image(
                alignment = Alignment.Center,
                painter = painterResource(userDevice.deviceImageId),
                contentDescription = "",
                modifier = Modifier
                    .size(150.dp)
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = userDevice.deviceName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
