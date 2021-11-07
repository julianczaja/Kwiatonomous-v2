package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.toFormattedString
import com.corrot.kwiatonomousapp.domain.model.Device

@Composable
fun DeviceItem(
    device: Device,
    onItemClick: (Device) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(device) }
    ) {
        Row(
            Modifier.fillMaxSize()
        ) {
            Box(
                Modifier
                    .background(color = Color.DarkGray)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.flower3),
                    contentDescription = "",
                    Modifier
                        .padding(8.dp)
                )
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp),
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
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp),
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
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
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
            }
        }
    }
}