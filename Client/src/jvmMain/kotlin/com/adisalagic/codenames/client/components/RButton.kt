package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.utils.cursorPointer

@Composable
fun RButton(buttonColors: ButtonColors = ButtonDefaults.buttonColors(), width: Dp = 200.dp, text: String, onClick: () -> Unit){
    Button(modifier = Modifier
        .width(width)
        .cursorPointer(),
        colors = buttonColors,
        onClick = onClick) {
        RText(text = text)
    }
}