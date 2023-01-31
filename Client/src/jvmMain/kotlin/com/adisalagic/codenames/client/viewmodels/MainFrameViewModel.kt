package com.adisalagic.codenames.client.viewmodels

import com.adisalagic.codenames.client.api.Manager
import com.adisalagic.codenames.client.api.objects.game.PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.RequestJoinTeam
import com.adisalagic.codenames.client.components.Side
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainFrameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameData(PlayerList(emptyList()), null))
    val state = _state.asStateFlow()

    private val eventListener = object : Manager.EventListener {
        override fun onGamePlayerList(gamePlayerList: PlayerList) {
            viewModelScope.launch {
                _state.update { it.copy(playerList = gamePlayerList) }
            }
        }

        override fun onGamePlayerInfo(playerInfo: PlayerInfo) {
            viewModelScope.launch {
                _state.update { it.copy(myself = playerInfo) }
            }
        }
    }

    init {
        Manager.setEventListener(eventListener)
    }

    fun sendBecomeMasterRequest(side: Side) {
        sendRequestJoinTeam("master", side)
    }

    fun sendBecomePlayerRequest(side: Side) {
        sendRequestJoinTeam("player", side)
    }

    private fun sendRequestJoinTeam(role: String, side: Side) {
        val me = _state.value.myself
        Manager.sendMessage(
            RequestJoinTeam(
                id = me!!.user.id,
                role = role,
                team = side.name.lowercase()
            )
        )
    }

    data class GameData(val playerList: PlayerList, val myself: PlayerInfo?)
}