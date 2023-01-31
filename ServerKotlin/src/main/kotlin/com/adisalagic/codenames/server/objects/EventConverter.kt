package com.adisalagic.codenames.server.objects

import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.requests.RequestJoin
import com.adisalagic.codenames.server.objects.requests.RequestJoinTeam
import com.google.gson.Gson

class EventConverter(
    private val onRequestJoin: (RequestJoin) -> Unit,
    private val onGamePlayerList: (PlayerList) -> Unit,
    private val onRequestJoinTeam: (RequestJoinTeam) -> Unit
) {

    private val gson = Gson()
    private val REQUEST_JOIN = "request_join"
    private val REQUEST_JOINTEAM = "request_jointeam"

    private val GAME_PLAYERLIST = "game_playerlist"


    fun provide(obj: String){
        if (obj.contains(GAME_PLAYERLIST)){
            onGamePlayerList(gson.fromJson(obj, PlayerList::class.java))
        } else if (obj.contains(REQUEST_JOINTEAM)){
            onRequestJoinTeam(gson.fromJson(obj, RequestJoinTeam::class.java))
        } else if (obj.contains(REQUEST_JOIN)){
            onRequestJoin(gson.fromJson(obj, RequestJoin::class.java))
        }
    }

    fun isJoinRequest(obj: String): Boolean {
        return obj.contains(REQUEST_JOIN)
    }
}