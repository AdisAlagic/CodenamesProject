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
    private val onGameStartOpenWord: (StartOpenWord) -> Unit,
    private val onGameTurnTimer: (TurnTimer) -> Unit,
    private val onGameStartSkipWord: (StartSkipWord) -> Unit
) {

    private val gson = Gson()

    fun provide(event: Int, obj: String) {
        when (event) {
            Event.GAME_PLAYER_LIST -> onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
            Event.GAME_PLAYER_INFO -> onGamePlayerInfo(gson.fromJson(obj, PlayerInfo::class.java))
            Event.GAME_STATE -> onGameState(gson.fromJson(obj, GameState::class.java))
            Event.GAME_TIMER -> onGameTimer(gson.fromJson(obj, TimerInfo::class.java))
            Event.GAME_START_OPEN_WORD -> onGameStartOpenWord(gson.fromJson(obj, StartOpenWord::class.java))
            Event.GAME_TURN_TIMER -> onGameTurnTimer(gson.fromJson(obj, TurnTimer::class.java))
            Event.GAME_START_SKIP_WORD -> onGameStartSkipWord(gson.fromJson(obj, StartSkipWord::class.java))
        }
    }
}