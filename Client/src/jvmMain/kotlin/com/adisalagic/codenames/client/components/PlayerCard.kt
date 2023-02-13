package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adisalagic.codenames.client.colors.BlackSide
import com.adisalagic.codenames.client.colors.TextColorBlack


enum class Direction {
    LEFT,
    RIGHT
}

@Composable
fun PlayerCard(playerName: String, playerColor: Color, direction: Direction = Direction.LEFT) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        shape = when (direction) {
            Direction.LEFT -> RoundedCornerShape(
                topStartPercent = 50,
                bottomStartPercent = 50,
                topEndPercent = 20,
                bottomEndPercent = 20
            )

            Direction.RIGHT -> RoundedCornerShape(
                topStartPercent = 20,
                bottomStartPercent = 20,
                topEndPercent = 50,
                bottomEndPercent = 50
            )
        },
        backgroundColor = Color(0xAC000000)
    ) {
        Row(
            horizontalArrangement = when (direction) {
                Direction.LEFT -> Arrangement.Start
                Direction.RIGHT -> Arrangement.End
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (direction) {
                Direction.LEFT -> {
                    Spacer(Modifier.width(20.dp))
                    InternalText(playerName)
                }

                Direction.RIGHT -> {
                    InternalText(playerName)
                    Spacer(Modifier.width(20.dp))
                }
            }

        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = when (direction) {
                Direction.LEFT -> Alignment.CenterStart
                Direction.RIGHT -> Alignment.CenterEnd
            }
        ) {
            PlayerIcon(playerColor)
        }
    }
}

@Composable
private fun InternalText(playerName: String) {
    RText(text = playerName, fontWeight = FontWeight.W400, fontColor = TextColorBlack, fontSize = 17.sp)
}