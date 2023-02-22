package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.api.objects.Role
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.colors.*
import com.adisalagic.codenames.client.utils.*
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

val model = ViewModelsStore.mainFrameViewModel

@Composable
fun PlayerList(side: Side) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(650.dp)
    ) {
        Column {
            
            Row {
                when (side) {
                    Side.BLUE -> {
                        Players(side)
                        BlueLine()
                    }

                    Side.BLACK,
                    Side.WHITE,
                    Side.RED -> {
                        RedLine()
                        Players(side)
                    }
                }

            }
        }

    }
}

@Composable
private fun RedLine() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(RedSide)
    )
}

@Composable
private fun BlueLine() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(BlueSide)
    )
}

@Composable
private fun Players(side: Side) {
    val data by model.state.collectAsState()
    val direction = if (side == Side.BLUE) {
        Direction.LEFT
    } else {
        Direction.RIGHT
    }
    Box(
        modifier = Modifier
            .background(
                when (side) {
                    Side.BLUE -> {
                        TeamBlueBackground
                    }

                    Side.RED -> {
                        TeamRedBackground
                    }

                    Side.BLACK,
                    Side.WHITE -> {
                        throw IllegalArgumentException("You cant have BLACK or NEUTRAL side here!")
                    }
                }
            )
            .fillMaxHeight()
            .width(198.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            val masters = data.playerList.getMasters()
            val master = masters.find { it.team == side.toTeamInt() }
            if (master != null) {
                PlayerCard(
                    playerName = master.nickname,
                    playerColor = Color.parseColor(master.color),
                    direction = direction
                )
            } else if (data.gameState?.state != State.STATE_PLAYING){
                FreeSlot("Стать ведущим") {
                    model.sendBecomeMasterRequest(side)
                }
            }
            MasterLine()
            val players = data.playerList.getPlayers(side.toTeamInt())
            val me = players.find { data.myself?.user?.id == it.id }
            if (me == null && data.gameState?.state != State.STATE_PLAYING) {
                FreeSlot("Стать игроком") {
                    model.sendBecomePlayerRequest(side)
                }
                Spacer(Modifier.height(5.dp))
            }
            val hght = if (me == null) {
                420.dp
            } else {
                440.dp
            }
            LazyColumn(modifier = Modifier.height(hght)) {
//                items(100){
//                    PlayerCard("$it", Color.random(), direction)
//                    Spacer(Modifier.height(5.dp))
//                }
                items(data.playerList.getPlayers(side.toTeamInt())) {
                    PlayerCard(
                        playerName = it.nickname.toString(),
                        playerColor = Color.parseColor(it.color),
                        direction = direction
                    )
                    Spacer(Modifier.height(5.dp))
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(100.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Logs(
                    when (side) {
                        Side.BLUE -> data.gameState?.blueScore?.logs ?: emptyList()
                        Side.RED -> data.gameState?.redScore?.logs ?: emptyList()
                        else -> { emptyList() }
                    },
                    (data.myself?.user?.role == Role.MASTER) &&
                            (data.gameState?.turn?.team == data.myself?.user?.team) &&
                            (data.myself?.user?.team == side.toTeamInt()) &&
                            (data.gameState?.turn?.role == Role.MASTER)
                )
            }
        }
    }
}

@Composable
private fun MasterLine() {
    Spacer(Modifier.height(10.dp))
    Divider(thickness = 1.dp, color = TextColorBlack)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun FreeSlot(slotText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        RText(
            modifier = Modifier
                .cursorPointer()
                .clickable { onClick() }
                .fillMaxWidth()
                .dashedBorder(
                    width = 2.dp,
                    color = TextColorBlack,
                    on = 3.dp,
                    off = 3.dp
                ),
            text = slotText,
            fontSize = 17.sp,
            fontColor = TextColorBlack,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Logs(logList: List<String>, shouldType: Boolean) {
    var text by remember {
        mutableStateOf("")
    }

    Column {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (shouldType) {
                BasicTextField(
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp){
                            model.sendLogRequest(text)
                            text = ""
                        }
                        return@onKeyEvent true
                    },
                    value = text,
                    textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
                    onValueChange = {
                        text = it
                    },
                    singleLine = true,
                ){
                    Box(modifier = Modifier.border(1.dp, Color.Black).fillMaxWidth().height(30.dp).padding(5.dp)){
                        it()
                    }
                }
            } else {
                RText(text = "Логи", fontColor = Color.White)
            }
        }
        MasterLine()
        LazyColumn(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            items(logList) {
                RText(text = it, fontColor = Color.White, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun Timer(timeInMillis: ULong){
    val time = timeInMillis.asTimeString()
    RText(text = time, fontColor = TextColor)
}