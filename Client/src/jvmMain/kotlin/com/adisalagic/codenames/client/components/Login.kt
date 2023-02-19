package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.NeutralSide
import com.adisalagic.codenames.client.colors.TextColorBlue
import com.adisalagic.codenames.client.utils.DEFAULT_CURSOR
import com.adisalagic.codenames.client.utils.HAND_CURSOR_ICON
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.viewmodels.LoginViewModel
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@Composable
fun LoginScreen() {
    val loginModel = ViewModelsStore.loginViewModel
    val data by loginModel.state.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (data.connectionState == LoginViewModel.ConnectionState.CONNECTING) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(400.dp)
                .height(300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TextColorBlue)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                TextBox(data.nickname, "Ник") {
                    loginModel.update(data.copy(nickname = it))
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextBox(data.address, "Адрес") {
                    loginModel.update(data.copy(address = it))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(modifier = Modifier
                    .width(200.dp)
                    .pointerHoverIcon(
                        if (data.connectionState != LoginViewModel.ConnectionState.CONNECTING){
                            HAND_CURSOR_ICON
                        }else{
                            DEFAULT_CURSOR
                        }
                    ),
                    enabled = data.connectionState != LoginViewModel.ConnectionState.CONNECTING,
                    onClick = {
                        loginModel.connect()
                    }) {
                    RText(text = "Войти")
                }
            }
        }
    }
}

@Composable
fun TextBox(init: String, title: String, onText: (text: String) -> Unit) {
    Column {
        RText(text = title, fontColor = Color.White)
        Input(init, onText = onText)
    }
}

@Composable
fun Input(init: String, onText: (text: String) -> Unit) {
    var value by remember {
        mutableStateOf(init)
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
                color = Color.White,
            ),
            cursorBrush = SolidColor(Color.White),
            value = value.ifEmpty {
                ""
            },
            onValueChange = {
                value = it
                onText(it)
            },
        )
    }
}