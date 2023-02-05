package com.adisalagic.codenames.server.objects

import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.requests.RequestJoin
import com.adisalagic.codenames.server.objects.requests.RequestJoinTeam
import com.adisalagic.codenames.server.objects.requests.RequestRestart
import com.adisalagic.codenames.server.objects.requests.RequestShuffleTeams
import com.google.gson.Gson

class EventConverter(
    private val onRequestJoin: (RequestJoin) -> Unit,
    private val onRequestJoinTeam: (RequestJoinTeam) -> Unit,
    private val onRequestRestart: (RequestRestart) -> Unit,
    private val onRequestShuffleTeams: (RequestShuffleTeams) -> Unit
) {

    private val gson = Gson()
    private val REQUEST_JOIN = "request_join"
    private val REQUEST_JOINTEAM = "request_jointeam"
    private val REQUEST_RESTART = "request_restart"
    private val REQUEST_SHUFFLETEAMS = "request_shuffleteams"



    fun provide(obj: String){
        if (obj.contains(REQUEST_JOINTEAM)){
            onRequestJoinTeam(gson.fromJson(obj, RequestJoinTeam::class.java))
        } else if (obj.contains(REQUEST_JOIN)){
            onRequestJoin(gson.fromJson(obj, RequestJoin::class.java))
        } else if (obj.contains(REQUEST_RESTART)){
            onRequestRestart(gson.fromJson(obj, RequestRestart::class.java))
        } else if (obj.contains(REQUEST_SHUFFLETEAMS)){
            onRequestShuffleTeams(gson.fromJson(obj, RequestShuffleTeams::class.java))
        }
    }

    fun isJoinRequest(obj: String): Boolean {
        return obj.contains(REQUEST_JOIN)
    }
}