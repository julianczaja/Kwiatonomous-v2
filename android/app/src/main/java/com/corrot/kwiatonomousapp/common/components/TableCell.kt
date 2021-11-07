package com.corrot.kwiatonomousapp.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    style: TextStyle = MaterialTheme.typography.body2,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .border(0.3.dp, Color.White, shape = RoundedCornerShape(4.dp))
            .weight(weight, true)
            .fillMaxSize()
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = style,
            modifier = Modifier.padding(4.dp)
        )
    }
}
