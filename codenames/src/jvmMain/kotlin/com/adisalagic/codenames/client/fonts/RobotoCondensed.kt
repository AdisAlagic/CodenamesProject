package com.adisalagic.codenames.client.fonts

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

val RobotoCondensed = FontFamily(
    Font(
        resource = "RobotoCondensed-Regular.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    ),
    Font(
        resource = "RobotoCondensed-Bold.ttf",
        weight = FontWeight.W700,
        style = FontStyle.Normal
    )
)