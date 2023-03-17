package com.adisalagic.codenames.client.components

import PlayerInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.api.objects.SKIP_WORD_ID
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.colors.ColorPlayerNotVisible
import com.adisalagic.codenames.client.colors.TextColorNotVisible
import com.adisalagic.codenames.client.utils.parseColor
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SkipWord(users: List<PlayerInfo>){
    val model =
        ViewModelsStore.mainFrameViewModel //compose-jb does not have method viewModel<>(), using object to store
    val data by model.state.collectAsState()
    Card(
        modifier = Modifier.width(500.dp).height(40.dp),
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            if (data.gameState?.state != State.STATE_PLAYING){
                return@Card
            }
            model.sendWordPressRequest(SKIP_WORD_ID, users.find { it.user.id == data.myself?.user?.id } == null)
        }
    ){
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                users.forEach {
                    PlayerIcon(Color.parseColor(it.user.color))
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            RText(text = "ЗАКОНЧИТЬ ХОД", fontColor = contentColorFor(MaterialTheme.colors.primary), textAlign = TextAlign.Center)
        }
    }
}