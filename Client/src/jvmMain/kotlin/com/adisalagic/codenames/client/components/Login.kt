package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.NeutralSide
import com.adisalagic.codenames.client.colors.TextColorBlue
import com.adisalagic.codenames.client.utils.cursorPointer

@Composable
fun LoginScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(400.dp)
                .height(300.dp)
                .background(TextColorBlue)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                TextBox("Ник") {}
                Spacer(modifier = Modifier.height(10.dp))
                TextBox("Адрес") {}
                Spacer(modifier = Modifier.height(10.dp))
                Button(modifier = Modifier
                    .width(200.dp)
                    .cursorPointer(),
                    onClick = {}){
                    RText(text = "Войти")
                }
            }
        }

    }
}

@Composable
fun TextBox(title: String, onText: (text: String) -> Unit) {
    Column(
    ) {
        RText(text = title)
        Input(onText = onText)
    }
}

@Composable
fun Input(onText: (text: String) -> Unit) {
    var value by remember {
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                NeutralSide
            )
    ) {
        BasicTextField(
            modifier = Modifier
                .width(200.dp)
                .padding(10.dp),
            singleLine = true,
            textStyle = TextStyle.Default.copy(
                color = Color.White
            ),
            value = if (value.isEmpty()) {
                ""
            } else {
                value
            },
            onValueChange = {
                value = it
            },
        )
    }
}