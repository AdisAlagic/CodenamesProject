package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.*

@Composable
fun Score() {
    Box(modifier = Modifier
        .width(200.dp),
        contentAlignment = Alignment.Center){
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScoreBox(Side.RED)
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier.width(10.dp).height(2.dp).background(NeutralSide)
            )
            Spacer(modifier = Modifier.width(20.dp))
            ScoreBox(Side.BLUE)
        }
    }
}

@Composable
private fun ScoreBox(side: Side) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(when (side){
                Side.BLUE -> BlueSide
                Side.RED -> RedSide
                Side.BLACK -> BlackSide
                Side.NEUTRAL -> NeutralSide
            }),
        contentAlignment = Alignment.Center
    ) {
        RText(
            text = "17",
            fontColor = when (side){
                Side.BLUE -> TextColorBlue
                Side.RED -> TextColorRed
                Side.BLACK -> TextColorBlack
                Side.NEUTRAL -> TextColorNeutral
            })
    }
}