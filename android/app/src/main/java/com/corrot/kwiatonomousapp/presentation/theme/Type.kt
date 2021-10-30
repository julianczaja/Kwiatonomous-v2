package com.corrot.kwiatonomousapp.presentation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.corrot.kwiatonomousapp.R

val RobotoFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Bold),
    Font(R.font.roboto_thin, FontWeight.Thin),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic)
)

val Typography = Typography(
    h1 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    h2 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    h3 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    body1 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = RobotoFamily,
        fontWeight = FontWeight.Thin,
        fontSize = 14.sp
    )
)