package com.adisalagic.codenames.client.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.TextColor

@Composable
fun AdminPanel() {
    var paused by remember {
        mutableStateOf(true)
    }
    Box(
        modifier = Modifier.width(400.dp).height(50.dp).padding(bottom = 10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()) {
            ButtonIcon("Перезапустить игру", "restart.svg") { model.sendRestartRequest() }
            ButtonIcon(
                if (paused) {
                    "Продолжить"
                } else {
                    "Приостановить"
                }, if (paused) {
                    "play.svg"
                } else {
                    "pause.svg"
                }
            ) {
                paused = !paused
            }
            ButtonIcon("Перемешать команды", "shuffle.svg") { model.sendRequestShuffleTeams() }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ButtonIcon(description: String, resourse: String, onClick: () -> Unit) {
    TooltipArea(
        tooltip = {
            Column{
                Box(modifier = Modifier.background(Color(0xFF2e2e2e)).padding(5.dp)){
                    RText(text = description, fontColor = TextColor)
                }
            }
        },
        tooltipPlacement = TooltipPlacement.CursorPoint(
            offset = DpOffset(1.dp, (-50).dp)
        )
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(resourse),
                contentDescription = null,
                tint = TextColor
            )
        }
    }

}