package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainFrame(){
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(top = 30.dp)
    ){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd){
            Spectators()
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ){
            PlayerList(Side.RED)
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ){
            PlayerList(Side.BLUE)
        }
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            WordGrid()
        }
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter){
            Score()
        }
    }
}