package com.corrot.kwiatonomousapp.presentation.device_details.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme
import java.time.LocalTime
import java.time.ZoneOffset


@Preview(
    "DeviceConfigurationPreviewLight",
    widthDp = 400,
    heightDp = 150,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DeviceConfigurationPreviewLight() {
    KwiatonomousAppTheme(darkTheme = false) {
        Surface {
            DeviceConfigurationItem(
                DeviceConfiguration(
                    "test_id",
                    30,
                    ZoneOffset.ofHours(1),
                    true,
                    2,
                    200,
                    LocalTime.of(12, 30)
                )
            )
        }
    }
}

@Composable
fun DeviceConfigurationItem(
    deviceConfiguration: DeviceConfiguration
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sleep time: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.sleepTimeMinutes}m",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Time zone: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "UTC${deviceConfiguration.timeZoneOffset}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Watering: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = if (deviceConfiguration.wateringOn) "on" else "off",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Watering interval: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringIntervalDays} days",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Watering amount: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringAmount} ml",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Watering time: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringTime}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}