package com.adisalagic.codenames.client.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.colors.RedSide
import com.adisalagic.codenames.client.colors.TeamRedBackground
import com.adisalagic.codenames.client.colors.TextColor
import com.adisalagic.codenames.client.utils.asMinutesAndSeconds
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.viewmodels.LoginViewModel
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainFrame() {
    val mainFrameViewModel = ViewModelsStore.mainFrameViewModel
    val data by mainFrameViewModel.state.collectAsState()
    val timerData by mainFrameViewModel.wordState.collectAsState()
    val hostIsNotHere = data.playerList.getHost() == null
    val playerListIsEmpty = data.playerList.users.isEmpty()
    val requester = remember { FocusRequester() }
    var adminVisibility by remember {
        mutableStateOf(false)
    }
    val blurRadius = if (playerListIsEmpty || hostIsNotHere) {
        10.dp
    } else {
        0.dp
    }
    LaunchedEffect(Unit) {
        requester.requestFocus()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val progressTimer = timerData.wordProgress
        if (progressTimer == 0f) {
            return@Box
        }
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = progressTimer,
            backgroundColor = Color.Transparent
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Spectators()
    }
    Box(
        modifier = Modifier.fillMaxSize().blur(radius = blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .padding(top = 30.dp)
            .focusRequester(requester)
            .onKeyEvent {
                if (it.key == Key.Tab && it.type == KeyEventType.KeyDown){
                    adminVisibility = !adminVisibility
                }
                true
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 9.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            RButton(
                buttonColors = ButtonDefaults.buttonColors(Color(0xFFaa312c), Color.White),
                width = 200.dp,
                text = "Отключиться"
            ) {
                model.disconnect()
            }
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
            modifier = Modifier.fillMaxSize().padding(start = 256.dp, top = 45.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Box(modifier = Modifier.background(Color.Gray, RoundedCornerShape(5.dp))) {
                RText(
                    modifier = Modifier.padding(5.dp), text = when (data.gameState?.state) {
                        State.STATE_NOT_STARTED -> "Не началась"
                        State.STATE_ENDED -> "Закончена"
                        State.STATE_PAUSED -> "Пауза"
                        State.STATE_PLAYING -> "В процессе"
                        else -> {
                            ""
                        }
                    }, fontColor = TextColor, fontSize = 17.sp
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(end = 267.dp, top = 45.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(modifier = Modifier.background(Color.Yellow, RoundedCornerShape(5.dp))) {
                RText(
                    modifier = Modifier.padding(5.dp), text = timerData.turnTimer.asMinutesAndSeconds(),
                    fontColor = Color.Black, fontSize = 17.sp,
                    textAlign = TextAlign.End
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Score()
        }
        if (data.myself?.user?.isHost == true) {
            AnimatedVisibility(
                visible = adminVisibility,
                enter = slideInHorizontally {
                    -it
                },
                exit = slideOutHorizontally(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow
                    )
                ) {
                    -it
                },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AdminPanel()
                }
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