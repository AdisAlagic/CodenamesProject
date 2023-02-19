package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.colors.RedSide
import com.adisalagic.codenames.client.colors.TeamRedBackground
import com.adisalagic.codenames.client.colors.TextColor
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@Composable
fun MainFrame() {
    val mainFrameViewModel = ViewModelsStore.mainFrameViewModel
    val data by mainFrameViewModel.state.collectAsState()
    val hostIsNotHere = data.playerList.getHost() == null
    val playerListIsEmpty = data.playerList.users.isEmpty()
    val blurRadius = if (playerListIsEmpty || hostIsNotHere) {
        10.dp
    } else {
        0.dp
    }
    Box(
        modifier = Modifier.fillMaxSize().blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .padding(top = 30.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            RButton(
                buttonColors = ButtonDefaults.buttonColors(Color(0xFFaa312c), Color.White),
                width = 190.dp,
                text = "Отключиться"
            ) {
                model.disconnect()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Spectators()
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            PlayerList(Side.RED)
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            PlayerList(Side.BLUE)
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            WordGrid()
        }
        Box(modifier = Modifier.fillMaxSize().padding(start = 255.dp, top = 45.dp),
            contentAlignment = Alignment.TopStart){
            Box(modifier = Modifier.background(Color.Gray, RoundedCornerShape(5.dp))){
                RText(modifier = Modifier.padding(5.dp), text = when(data.gameState?.state){
                    GameState.STATE_NOT_STARTED -> "Не началась"
                    GameState.STATE_ENDED -> "Закончена"
                    GameState.STATE_PAUSED -> "Пауза"
                    GameState.STATE_PLAYING -> "В процессе"
                    else -> {""}
                }, fontColor = TextColor, fontSize = 17.sp)
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Score()
        }
        if (data.myself?.user?.isHost == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AdminPanel()
            }
        }
    }
    if (playerListIsEmpty) {
        MessageBoxAdapter("Получаю данные об игроках")
    } else if (hostIsNotHere) {
        MessageBoxAdapter("Ожидание подключения хоста")
    }

}

@Composable
private fun MessageBoxAdapter(text: String) {
    val trsp = Color(0x80000000)
    Box(
        modifier = Modifier.fillMaxSize().background(trsp),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.matchParentSize().clickable(
            interactionSource = MutableInteractionSource(),
            indication = null
        ) {})
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MessageBox(text)
            RButton(text = "Отмена") {
                model.disconnect()
            }
        }
    }
}