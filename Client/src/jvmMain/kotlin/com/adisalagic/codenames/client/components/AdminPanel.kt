package com.adisalagic.codenames.client.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.colors.TextColor
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import java.io.File

@Composable
fun AdminPanel() {
    val model = ViewModelsStore.mainFrameViewModel
    val data by model.state.collectAsState()
    val paused = (data.gameState?.state == State.STATE_PAUSED)

    Box(
        modifier = Modifier.width(400.dp)
            .height(50.dp)
            .padding(bottom = 10.dp)
            .background(Color(0f, 0f, 0f, 0.5f), shape = RoundedCornerShape(10.dp))
            .clickable(interactionSource = MutableInteractionSource(), indication = null){}
            .clip(RoundedCornerShape(10.dp)),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
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
                model.sendPauseResumeRequest()
            }
            ButtonIcon("Перемешать команды", "shuffle.svg") { model.sendRequestShuffleTeams() }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ButtonIcon(description: String, resourse: String, onClick: () -> Unit) {
    val file = File("./src/jvmMain/resources/$resourse")
    TooltipArea(
        tooltip = {
            Column {
                Box(modifier = Modifier.background(Color(0xFF2e2e2e)).padding(5.dp)) {
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
//                painter = loadSvgPainter(file.inputStream(), LocalDensity.current),
                painter = painterResource(resourse),
                contentDescription = null,
                tint = TextColor
            )
        }
    }

}