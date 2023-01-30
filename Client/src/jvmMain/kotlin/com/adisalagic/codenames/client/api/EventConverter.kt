package com.adisalagic.codenames.client.api

import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.RequestJoin
import com.google.gson.Gson

class EventConverter(
    private val onGamePlayerList: (PlayerList) -> Unit
) {

    private val gson = Gson()

    private val GAME_PLAYERLIST = "game_playerlist"

    fun provide(obj: String){

        if (obj.contains(GAME_PLAYERLIST)){
            onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
        }
    }
}