package com.corrot.kwiatonomousapp.presentation.dasboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.corrot.kwiatonomousapp.R
import com.corrot.kwiatonomousapp.common.components.TableCell

@Composable
fun DeviceUpdateHeaderRowItem() {
    Row(
        Modifier
            .background(Color.DarkGray)
    ) {
        TableCell(
            text = stringResource(R.string.date),
            style = MaterialTheme.typography.body1,
            weight = 0.3f
        )
        TableCell(
            text = stringResource(R.string.battery),
            style = MaterialTheme.typography.body1,
            weight = 0.30f
        )
        TableCell(
            text = stringResource(R.string.temperature_abbr),
            style = MaterialTheme.typography.body1,
            weight = 0.2f
        )
        TableCell(
            text = stringResource(R.string.humidity_abbr),
            style = MaterialTheme.typography.body1,
            weight = 0.2f
        )
    }
}