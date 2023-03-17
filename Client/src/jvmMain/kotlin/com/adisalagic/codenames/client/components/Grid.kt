package com.adisalagic.codenames.client.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adisalagic.codenames.client.api.objects.Role
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import kotlin.random.Random

@Composable
fun WordGrid() {
    val model = ViewModelsStore.mainFrameViewModel
    val data by model.state.collectAsState()
    val size = 6
    Column {
        for (i in 0 until size) {
            Row {
                for (j in 0 until size) {
                    if (data.gameState != null) {
                        val word = data.gameState!!.words[i * size + j]
                        WordBox(word)
                        Spacer(Modifier.width(10.dp))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (data.gameState?.turn?.team == data.myself?.user?.team &&
            data.myself?.user?.role != Role.MASTER &&
            data.gameState?.turn?.role == Role.PLAYER
        ) {
            SkipWord(data.gameState?.skipWord!!)
        }
    }
}

private fun randomSide(): Side {
    val vals = Side.values()
    val size = vals.size
    return vals[Random.nextInt(0, size)]
}