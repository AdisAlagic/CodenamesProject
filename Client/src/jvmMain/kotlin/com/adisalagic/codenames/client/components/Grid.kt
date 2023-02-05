package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import kotlin.random.Random

@Composable
fun WordGrid() {
    val model = ViewModelsStore.mainFrameViewModel
    val data by model.state.collectAsState()
    val size = 6
    val visible = data.myself?.user?.role == "master"
    Column {
        for (i in 1..size) {
            Row {
                for (j in 1..6){
                    WordBox("???", randomSide(), visible)
                    Spacer(Modifier.width(10.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun randomSide(): Side {
    val vals = Side.values()
    val size = vals.size
    return vals[Random.nextInt(0, size)]
}