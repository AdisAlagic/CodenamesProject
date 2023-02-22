package com.adisalagic.codenames.client.api

import PlayerInfo
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

    companion object {
        private const val GAME_PLAYERLIST = "game_playerlist"
        private const val GAME_PLAYERINFO = "game_playerinfo"
        private const val GAME_STATE = "game_state"
        private const val GAME_TIMER = "game_timer"
        private const val GAME_START_OPENWORD = "game_start_openword"
    }

    fun provide(obj: String) {
        if (obj.contains(GAME_PLAYERLIST)) {
            onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
        } else if (obj.contains(GAME_PLAYERINFO)) {
            onGamePlayerInfo(gson.fromJson(obj, PlayerInfo::class.java))
        } else if (obj.contains(GAME_STATE)){
            onGameState(gson.fromJson(obj, GameState::class.java))
        } else if (obj.contains(GAME_TIMER)){
            onGameTimer(gson.fromJson(obj, TimerInfo::class.java))
        } else if (obj.contains(GAME_START_OPENWORD)){
            onGameStartOpenWord(gson.fromJson(obj, StartOpenWord::class.java))
        }
    }
}