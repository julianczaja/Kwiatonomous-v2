package com.corrot.kwiatonomousapp.common.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corrot.kwiatonomousapp.common.components.chart.LineChartDateType
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Preview(
    "CustomRadioGroupPreviewLight",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CustomRadioGroupPreviewLight() {
    val options = LineChartDateType.entries.map { it.name }
    var selectedOption by remember { mutableIntStateOf(2) }
    val onOptionSelected = { index: Int -> selectedOption = index }

    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            CustomRadioGroup(
                options = options,
                selectedIndex = selectedOption,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
fun CustomRadioGroup(
    selectedIndex: Int,
    options: List<String>,
    onOptionSelected: (ordinal: Int) -> Unit
) {
    Row {
        options.forEachIndexed { index, s ->
            Surface(
                color = when (index) {
                    selectedIndex -> MaterialTheme.colors.primary
                    else -> Color.Transparent
                },
                contentColor = when (index) {
                    selectedIndex -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onSurface
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when (index) {
                        selectedIndex -> MaterialTheme.colors.primary
                        else -> Color.LightGray
                    }
                ),
                shape = CircleShape,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Row {
                    Text(
                        text = options[index].uppercase(),
                        style = typography.overline.copy(fontSize = 9.sp),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onOptionSelected(index) }
                    )
                }
            }
        }
    }
}