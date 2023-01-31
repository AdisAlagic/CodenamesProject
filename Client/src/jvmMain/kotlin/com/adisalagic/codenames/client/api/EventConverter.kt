package com.adisalagic.codenames.client.api

import com.adisalagic.codenames.client.api.objects.game.PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.google.gson.Gson

class EventConverter(
    private val onGamePlayerList: (PlayerList) -> Unit,
    private val onGamePlayerInfo: (PlayerInfo) -> Unit
) {

    private val gson = Gson()

    private val GAME_PLAYERLIST = "game_playerlist"
    private val GAME_PLAYERINFO = "game_playerinfo"

    fun provide(obj: String){

        if (obj.contains(GAME_PLAYERLIST)){
            onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
        }
        if (obj.contains(GAME_PLAYERINFO)){
            onGamePlayerInfo(gson.fromJson(obj, PlayerInfo::class.java))
        }
    }
}