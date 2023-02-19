package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.*
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore

@Composable
fun Score() {
    val model = ViewModelsStore.mainFrameViewModel
    val data by model.state.collectAsState()
    Box(
        modifier = Modifier
            .width(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScoreBox(
                Side.RED,
                data.gameState?.redScore?.score?.toString(10) ?: "??",
                data.gameState?.turn?.team == "red"
            )
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier.width(10.dp).height(2.dp).background(NeutralSide)
            )
            Spacer(modifier = Modifier.width(20.dp))
            ScoreBox(Side.BLUE, data.gameState?.blueScore?.score?.toString(10) ?: "??", data.gameState?.turn?.team == "blue")
        }
    }
}

@Composable
private fun ScoreBox(side: Side, score: String, active: Boolean) {
    val color = if (active) {
        Color.White
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(15.dp))
            .border(2.dp, color, RoundedCornerShape(15.dp))
            .background(
                when (side) {
                    Side.BLUE -> BlueSide
                    Side.RED -> RedSide
                    Side.BLACK -> BlackSide
                    Side.WHITE -> NeutralSide
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        RText(
            text = score,
            fontColor = when (side) {
                Side.BLUE -> TextColorBlue
                Side.RED -> TextColorRed
                Side.BLACK -> TextColorBlack
                Side.WHITE -> TextColorNeutral
            }
        )
    }
}