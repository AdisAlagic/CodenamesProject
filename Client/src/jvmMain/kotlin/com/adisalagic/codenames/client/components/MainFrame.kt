package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.TextColor
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@Composable
fun MainFrame() {
    val mainFrameViewModel = ViewModelsStore.mainFrameViewModel
    val data by mainFrameViewModel.state.collectAsState()

    if (data.playerList.users.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            MessageBox("Получаю данные об игроках")
        }
    }
    if (data.playerList.getHost() == null){
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            MessageBox("Ожидание подключения хоста")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(top = 30.dp)
    ) {
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Score()
        }
        if (/*data.myself?.user?.isHost ==*/ true){
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter){
                AdminPanel()
            }
        }
    }
}