package com.adisalagic.codenames.client.viewmodels

import com.adisalagic.codenames.client.api.Manager
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.api.objects.game.PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.AdminRequest
import com.adisalagic.codenames.client.api.objects.requests.RequestJoinTeam
import com.adisalagic.codenames.client.api.objects.requests.RequestRestart
import com.adisalagic.codenames.client.api.objects.requests.RequestShuffleTeams
import com.adisalagic.codenames.client.components.Side
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class MainFrameViewModel : ViewModel() {
    private val _state = reset()
    val state = _state.asStateFlow()
    val logger: Logger = LogManager.getLogger(this::class.simpleName)


    private val eventListener = object : Manager.EventListener {
        override fun onGamePlayerList(gamePlayerList: PlayerList) {
            viewModelScope.launch {
                _state.update { it.copy(playerList = gamePlayerList) }
                logger.debug("Data updated")
                val me = gamePlayerList.users.find { _state.value.myself?.user?.id == it.id }
                if (me != null){
                    _state.update { it.copy(myself = PlayerInfo(PlayerInfo.User(
                        color = me.color,
                        id = me.id,
                        isHost = me.isHost,
                        nickname = me.nickname,
                        role = me.role,
                        team = me.team
                    ))) }
                }
            }
        }



        override fun onGamePlayerInfo(playerInfo: PlayerInfo) {
            viewModelScope.launch {
                _state.update { it.copy(myself = playerInfo) }
            }
        }

        override fun onGameState(gameState: GameState) {
            viewModelScope.launch {
                _state.update { it.copy(gameState = gameState) }
            }
        }
    }

    fun reset(): MutableStateFlow<GameData> {
        return MutableStateFlow(GameData(PlayerList(emptyList()), null, null))
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

    fun sendBecomeSpectatorRequest(){
        sendRequestJoinTeam("spectator", Side.NEUTRAL)
    }

    fun sendRequestShuffleTeams(){
        if (_state.value.myself == null){
            return
        }
        val me = _state.value.myself
        Manager.sendMessage(RequestShuffleTeams(AdminRequest.Host(me!!.user.id)))
    }

    fun sendRestartRequest(){
        if (_state.value.myself == null){
            return
        }
        val me = _state.value.myself
        Manager.sendMessage(RequestRestart(AdminRequest.Host(me!!.user.id)))
    }

    private fun sendRequestJoinTeam(role: String, side: Side) {
        logger.debug("Creating request for side or team change")
        val me = _state.value.myself
        if (me == null){
            Manager.disconnect()
        }
        var team = side.name.lowercase()
        if (side == Side.NEUTRAL){
            team = "none"
        }
        Manager.sendMessage(
            RequestJoinTeam(
                id = me!!.user.id,
                role = role,
                team = team
            )
        )
    }

    data class GameData(val playerList: PlayerList, val myself: PlayerInfo?, val gameState: GameState?)
}