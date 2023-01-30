package com.adisalagic.codenames.client.viewmodels

import com.adisalagic.codenames.client.api.Manager
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.RequestJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainFrameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameData(PlayerList(emptyList())))
    val state = _state.asStateFlow()

    private val eventListener = object : Manager.EventListener {
        override fun onGamePlayerList(gamePlayerList: PlayerList) {
            viewModelScope.launch {
                _state.update { it.copy(playerList = gamePlayerList) }
            }
        }
    }

    init {
        Manager.setEventListener(eventListener)
    }

    data class GameData(val playerList: PlayerList)
}