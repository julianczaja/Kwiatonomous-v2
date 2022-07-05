package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppSettingsToggleItem(
    title: String,
    isChecked: Boolean,
    onToggleClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1
        )

        Switch(
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
            checked = isChecked,
            onCheckedChange = onToggleClicked
        )
    }
}