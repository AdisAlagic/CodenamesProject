package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.api.objects.Role
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.colors.*
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.utils.isWholeTeamClicked
import com.adisalagic.codenames.client.utils.parseColor
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import java.nio.CharBuffer
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.ceil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordBox(word: GameState.Word) {
    val model = ViewModelsStore.mainFrameViewModel //compose-jb does not have method viewModel<>(), using object to store
    val data by model.state.collectAsState()

    val visible = word.visible || data.myself?.user?.role == Role.MASTER
    val side = Side.valueOf(word.side.toString().uppercase())

    val animationStart = word.animationStart
    val animationEnd = word.animationEnd

    var progress by remember {
        mutableStateOf(0f)
    }

    Card(
        modifier = Modifier
            .cursorPointer()
            .width(160.dp)
            .height(95.dp),
        onClick = {
            if (visible) {
                return@Card
            }
            val isTeam = data.gameState?.turn?.team != data.myself?.user?.team
            val isMaster = data.gameState?.turn?.role == Role.MASTER
            if (isTeam || isMaster) {
                return@Card
            }
            if (data.gameState?.state != State.STATE_PLAYING){
                return@Card
            }
            if (model.wordTimer != null){
                model.wordTimer?.cancel()
                model.wordTimer = null
                progress = 0f
            }
            model.sendWordPressRequest(word.id, word.usersPressed.find { it.id == data.myself!!.user.id } == null)

        },
        backgroundColor = getBackgroundTileColor(visible, side)
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                word.usersPressed.forEach {
                    PlayerIcon(Color.parseColor(it.color))
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            var string = (word.name as String).uppercase()
            if (string.length > 11) {
                val half = ceil((string.length / 2).toDouble()).toInt()
                string = string.substring(0, half) + "\n" + string.substring(half, string.length)
            }
            RText(text = string, fontColor = getTextColor(visible, side), textAlign = TextAlign.Center)
        }
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.fillMaxSize()
        ) {
            if (progress != 0f) {
                LinearProgressIndicator(
                    progress = progress
                )
            }
        }
    }

    if (animationStart != null) {
        val standardTimeAnimation = 4000uL
        val animationTime: ULong = if (animationEnd != null) {
            animationEnd - animationStart + 1000uL
        } else {
            standardTimeAnimation
        }
        if (!word.isWholeTeamClicked(data.playerList)){
            model.wordTimer?.cancel()
            model.wordTimer = null
            progress = 0f
            model.deleteAnimationTime(word)
        } else {
            model.wordTimer = animateProgress(animationTime.toDouble(), onProgress = {
                progress = it
            }, onDone = {
                model.wordTimer = null
                progress = 0f
                model.deleteAnimationTime(word)
            })
        }
    }

}

private fun getBackgroundTileColor(isVisible: Boolean = false, side: Side): Color {
    if (isVisible) {
        return when (side) {
            Side.BLUE -> BlueSide
            Side.RED -> RedSide
            Side.BLACK -> BlackSide
            Side.WHITE -> NeutralSide
        }
    }
    return ColorPlayerNotVisible
}

private fun getTextColor(visible: Boolean, side: Side): Color {
    if (!visible) {
        return TextColorNotVisible
    }
    return when (side) {
        Side.BLUE -> TextColorBlue
        Side.RED -> TextColorRed
        Side.BLACK -> TextColorBlack
        Side.WHITE -> TextColorNeutral
    }
}

enum class Side {
    BLUE,
    RED,
    BLACK,
    WHITE,
}


fun animateProgress(time: Double, onProgress: (progress: Float) -> Unit = {}, onDone: () -> Unit = {}): Timer {
    //somewhere is error, but I cant find it
    var progress: Float = 0f
    var est = 0.0 //time in milliseconds
    val finalTime = time / 1.75
    return timer(initialDelay = 0L, period = 1L) {
        if (progress < 1) {
            val result = est / (finalTime)
            progress = result.toFloat()
            onProgress(progress)
//            LogManager.getLogger("WordBox").debug("Progress: $progress and est is $est")
            est++
        } else {
            onProgress(0f)
            onDone()
            this.cancel()
        }
    }
}
