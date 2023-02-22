package com.adisalagic.codenames.client.api

import PlayerInfo
import com.adisalagic.codenames.client.api.objects.Event
import com.adisalagic.codenames.client.api.objects.game.*
import com.google.gson.Gson

class EventConverter(
    private val onGamePlayerList: (PlayerList) -> Unit,
    private val onGamePlayerInfo: (PlayerInfo) -> Unit,
    private val onGameState: (GameState) -> Unit,
    private val onGameTimer: (TimerInfo) -> Unit,
    private val onGameStartOpenWord: (StartOpenWord) -> Unit
) {

    private val gson = Gson()

    fun provide(event: Int, obj: String) {
        when (event) {
            Event.GAME_PLAYER_LIST -> onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
            Event.GAME_PLAYER_INFO -> onGamePlayerInfo(gson.fromJson(obj, PlayerInfo::class.java))
            Event.GAME_STATE -> onGameState(gson.fromJson(obj, GameState::class.java))
            Event.GAME_TIMER -> onGameTimer(gson.fromJson(obj, TimerInfo::class.java))
            Event.GAME_START_OPEN_WORD -> onGameStartOpenWord(gson.fromJson(obj, StartOpenWord::class.java))
        }
    }
}