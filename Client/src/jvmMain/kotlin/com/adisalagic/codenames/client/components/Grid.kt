package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun WordGrid() {
    val size = 6;
    Column {
        for (i in 1..size) {
            Row {
                for (j in 1..6){
                    WordBox((i*j).toString(), randomSide(), false)
                    Spacer(Modifier.width(10.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun randomSide(): Side {
    val vals = Side.values()
    val size = vals.size;
    return vals[Random.nextInt(0, size)]
}