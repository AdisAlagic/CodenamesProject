package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.colors.*
import com.adisalagic.codenames.client.utils.cursorPointer
import com.adisalagic.codenames.client.utils.dashedBorder
import com.adisalagic.codenames.client.utils.random

@Composable
fun PlayerList(side: Side) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(650.dp)
    ) {
        Row {
            when (side){
                Side.BLUE -> {
                    Players(side)
                    BlueLine()
                }
                Side.BLACK,
                Side.NEUTRAL,
                Side.RED -> {
                    RedLine()
                    Players(side)
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
private fun BlueLine(){
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
    var direction = if (side == Side.BLUE){
        Direction.LEFT
    }else{
        Direction.RIGHT
    }
    Box(
        modifier = Modifier
            .background(
                when (side){
                    Side.BLUE -> {
                        TeamBlueBackground
                    }
                    Side.RED -> {
                        TeamRedBackground}
                    Side.BLACK,
                    Side.NEUTRAL -> { throw IllegalArgumentException("You cant have BLACK or NEUTRAL side here!")}
                }
            )
            .fillMaxHeight()
            .width(198.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            FreeSlot("Стать ведущим") {}
            MasterLine()
            for (i in 1..10){
                PlayerCard("$i", Color.random(), direction)
                Spacer(Modifier.height(5.dp))
            }
            FreeSlot("Стать игроком"){}
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
@Composable
private fun Logs(){

}