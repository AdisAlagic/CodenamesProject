package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.colors.NeutralSide
import com.adisalagic.codenames.client.colors.TextColor
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
            data.playerList.users.forEach {
                if (it.role.equals("spectator")){
                    RText(
                        text = it.nickname,
                        fontSize = 15.sp,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }
        }
    }
}