package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.colors.TextColor

@Composable
fun MessageBox(text: String){
    Box(modifier = Modifier.height(100.dp),
        contentAlignment = Alignment.Center){
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.width(20.dp))
            RText(text = text, fontColor = TextColor)
        }
    }
}