package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import com.adisalagic.codenames.client.utils.*
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WordBox(word: GameState.Word) {
    val model =
        ViewModelsStore.mainFrameViewModel //compose-jb does not have method viewModel<>(), using object to store
    val data by model.state.collectAsState()

    val visible = word.visible || data.myself?.user?.role == Role.MASTER
    val side = word.side.toSide()

    val animationStart = word.animationStart
    val animationEnd = word.animationEnd

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
            if (data.gameState?.state != State.STATE_PLAYING) {
                return@Card
            }
            if (model.wordTimer != null) {
                model.wordTimer?.end()?.reset()
                model.wordTimer = null
                model.updateWordTimerProgress(0f)
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
            var string = word.name.uppercase()
            if (string.length > 11) {
                val half = ceil((string.length / 2).toDouble()).toInt()
                string = string.substring(0, half) + "\n" + string.substring(half, string.length)
            }
            RText(text = string, fontColor = getTextColor(visible, side), textAlign = TextAlign.Center)
        }
    }

    if (animationStart != null) {
        val standardTimeAnimation = 4000uL
        val animationTime: ULong = if (animationEnd != null) {
            animationEnd - animationStart + 1000uL
        } else {
            standardTimeAnimation
        }
        model.wordTimer?.end()?.reset()
        if (!word.isWholeTeamClicked(data.playerList)) {
            model.wordTimer = null
            model.updateWordTimerProgress(0f)
            model.deleteAnimationTime(word)
        } else {
            model.wordTimer = animateProgress(animationTime.toDouble(), onProgress = {
                model.updateWordTimerProgress(it)
            }, onDone = {
                model.wordTimer = null
                model.updateWordTimerProgress(0f)
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


fun animateProgress(time: Double, onProgress: (progress: Float) -> Unit = {}, onDone: () -> Unit = {}): CountDownTimer {
    //somewhere is error, but I cant find it
    var progress: Float = 0f
    var est = 0.0 //time in milliseconds
    val finalTime = time / 1.0
    return CountDownTimer(
        duration = finalTime.milliseconds,
        eventTriggerInterval = 1.milliseconds,
    ){
        onProgress((it.toDouble(DurationUnit.MILLISECONDS).toFloat() / finalTime).toFloat())
    }.start()
}
