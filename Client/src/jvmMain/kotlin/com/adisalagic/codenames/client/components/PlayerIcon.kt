package com.adisalagic.codenames.client.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerIcon(color: Color) {
    Box(
        modifier = Modifier
            .size(15.dp)
            .border(BorderStroke(1.dp, Color.White), CircleShape)
            .clip(CircleShape)
            .background(color)
    )
    Spacer(modifier = Modifier.width(2.dp))
}