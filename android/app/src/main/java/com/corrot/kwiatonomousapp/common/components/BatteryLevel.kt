package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.corrot.kwiatonomousapp.R

@Composable
fun BatteryLevel(batteryLevel: Int) {

    fun getBatteryDrawable(): Int {
        return when {
            batteryLevel >= 90 -> R.drawable.full_battery
            batteryLevel in 60..89 -> R.drawable.charged_battery
            batteryLevel in 30..60 -> R.drawable.half_battery
            else -> R.drawable.low_battery
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = getBatteryDrawable()),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.battery_level_format).format(batteryLevel),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}