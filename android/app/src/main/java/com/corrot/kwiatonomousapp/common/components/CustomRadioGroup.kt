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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corrot.kwiatonomousapp.presentation.theme.KwiatonomousAppTheme


@Preview(
    "CustomRadioGroupPreviewLight",
    widthDp = 400,
    heightDp = 200,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CustomRadioGroupPreviewLight() {
    val options = LineChartDateType.values().map { it.name }
    var selectedOption by remember {
        mutableStateOf("")
    }
    val onOptionSelected = { text: String ->
        selectedOption = text
    }

    KwiatonomousAppTheme(darkTheme = true) {
        Surface {
            CustomRadioGroup(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
fun CustomRadioGroup(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (option: String) -> Unit
) {
    Row {
        options.forEach { option ->
            Surface(
                color = when (option) {
                    selectedOption -> MaterialTheme.colors.primary
                    else -> Color.Transparent
                },
                contentColor = when (option) {
                    selectedOption -> MaterialTheme.colors.onPrimary
                    else -> MaterialTheme.colors.onSurface
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = when (option) {
                        selectedOption -> MaterialTheme.colors.primary
                        else -> Color.LightGray
                    }
                ),
                shape = CircleShape,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Row {
                    Text(
                        text = option,
                        style = typography.body2.copy(fontSize = 8.sp, fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                onOptionSelected(option)
                            }
                    )
                }
            }
        }
    }
}