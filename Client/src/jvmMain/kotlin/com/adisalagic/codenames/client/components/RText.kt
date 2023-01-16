package com.adisalagic.codenames.client.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.colors.BlackSide
import com.adisalagic.codenames.client.fonts.RobotoCondensed


@Composable
fun RText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.W700,
    fontFamily: FontFamily = RobotoCondensed,
    fontSize: TextUnit = 23.sp,
    textAlign: TextAlign? = null,
    fontColor: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        fontSize = fontSize,
        color = fontColor
    )
}

