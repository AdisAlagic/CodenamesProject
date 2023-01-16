package com.adisalagic.codenames.client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.NeutralSide

@Composable
fun Spectators() {
    Box(
        modifier = Modifier
            .height(30.dp)
            .background(NeutralSide)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {

        }
    }
}