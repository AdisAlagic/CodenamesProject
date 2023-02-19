package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.colors.NeutralSide
import com.adisalagic.codenames.client.colors.TextColor
import com.adisalagic.codenames.client.colors.TextColorBlack
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.utils.dashedBorder
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@Composable
fun Spectators() {
    val mainFrameViewModel = ViewModelsStore.mainFrameViewModel
    val data by mainFrameViewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .height(30.dp)
            .background(NeutralSide)
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
        ) {
            data.playerList.getSpectators().forEach {
                RText(
                    text = it.nickname,
                    fontSize = 15.sp,
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (!data.myself?.user?.role.equals("spectator", true) &&
                data.gameState?.state != GameState.STATE_PLAYING) {
                RText(
                    text = "Стать наблюдателем",
                    fontSize = 15.sp,
                    modifier = Modifier.dashedBorder(
                        width = 2.dp,
                        color = TextColorBlack,
                        on = 3.dp,
                        off = 3.dp
                    ).clickable {
                        model.sendBecomeSpectatorRequest()
                    }.cursorPointer(),
                )
            }
        }
    }
}