package com.adisalagic.codenames.client.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.*
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.utils.random
import java.util.*
import kotlin.concurrent.timer

@OptIn(ExperimentalMaterialApi::class)
@Preview()
@Composable
fun WordBox(word: String, side: Side, visible: Boolean = false) {
    var progress by remember {
        mutableStateOf(0f)
    }
    var clicked by remember {
        mutableStateOf(false)
    }
    var timer: Timer? = null;
    Card(
        modifier = Modifier
            .cursorPointer()
            .width(160.dp)
            .height(95.dp),
        onClick = {
            if (visible){
                return@Card
            }
            if (clicked) {
                timer?.cancel()
                progress = 0f
                clicked = false
            } else {
                timer = animateProgress(
                    1000.0,
                    onProgress = {
                        progress = it
                    },
                    onDone = {
                        clicked = false
                    })
                clicked = true
            }
        },
        backgroundColor = getBackgroundTileColor(visible, side)
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                PlayerIcon(Color.random())
                PlayerIcon(Color.random())
                PlayerIcon(Color.random())
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            RText(text = word.uppercase(), fontColor = getTextColor(visible, side))
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
}

private fun getBackgroundTileColor(isVisible: Boolean = false, side: Side): Color {
    if (isVisible) {
        return when (side) {
            Side.BLUE -> BlueSide
            Side.RED -> RedSide
            Side.BLACK -> BlackSide
            Side.NEUTRAL -> NeutralSide
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
        Side.NEUTRAL -> TextColorNeutral
    }
}

enum class Side {

    BLUE,
    RED,
    BLACK,
    NEUTRAL,
}


fun animateProgress(time: Double, onProgress: (progress: Float) -> Unit, onDone: () -> Unit): Timer {
    //somewhere is error, but I cant find it
    var progress = 0.00001f
    var est = 0.0 //time in milliseconds
    return timer(initialDelay = 0L, period = 1L) {
        if (est != time) {
            val result = est / time
            progress = result.toFloat()
            onProgress(progress)
            est++
        } else {
            onProgress(0f)
            onDone()
            this.cancel()
        }
    }
}