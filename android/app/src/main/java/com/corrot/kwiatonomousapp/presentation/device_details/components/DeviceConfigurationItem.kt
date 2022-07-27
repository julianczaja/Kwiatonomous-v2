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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
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
                        text = stringResource(R.string.sleep_time) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.sleepTimeMinutes}m",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.time_zone) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "UTC${deviceConfiguration.timeZoneOffset}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.watering) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = if (deviceConfiguration.wateringOn)
                            stringResource(R.string.on_abbr)
                        else
                            stringResource(R.string.off_abbr),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.watering_interval) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringIntervalDays} days",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.watering_amount) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringAmount} ml",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.watering_time) + ": ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                    )
                    Text(
                        text = "${deviceConfiguration.wateringTime}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Light,
                    )
                }
            }
        }
    }
}