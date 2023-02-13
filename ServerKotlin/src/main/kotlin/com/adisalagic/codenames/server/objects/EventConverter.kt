package com.adisalagic.codenames.server.objects

import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.requests.*
import com.google.gson.Gson

class EventConverter(
    private val onRequestJoin: (RequestJoin) -> Unit,
    private val onRequestJoinTeam: (RequestJoinTeam) -> Unit,
    private val onRequestRestart: (RequestRestart) -> Unit,
    private val onRequestShuffleTeams: (RequestShuffleTeams) -> Unit,
    private val onRequestTimer: (RequestTimer) -> Unit,
    private val onRequestPressWord: (RequestPressWord) -> Unit,
) {

    companion object {
        const val REQUEST_PRESSWORD = "request_pressword"
        const val REQUEST_JOIN = "request_join"
        const val REQUEST_JOINTEAM = "request_jointeam"
        const val REQUEST_RESTART = "request_restart"
        const val REQUEST_SHUFFLETEAMS = "request_shuffleteams"
        const val REQUEST_TIMER = "request_timer"
    }

    private val gson = Gson()




    fun provide(obj: String){
        if (obj.contains(REQUEST_JOINTEAM)){
            onRequestJoinTeam(gson.fromJson(obj, RequestJoinTeam::class.java))
        } else if (obj.contains(REQUEST_JOIN)){
            onRequestJoin(gson.fromJson(obj, RequestJoin::class.java))
        } else if (obj.contains(REQUEST_RESTART)){
            onRequestRestart(gson.fromJson(obj, RequestRestart::class.java))
        } else if (obj.contains(REQUEST_SHUFFLETEAMS)){
            onRequestShuffleTeams(gson.fromJson(obj, RequestShuffleTeams::class.java))
        } else if (obj.contains(REQUEST_TIMER)){
            onRequestTimer(gson.fromJson(obj, RequestTimer::class.java))
        } else if (obj.contains(REQUEST_PRESSWORD)){
            onRequestPressWord(gson.fromJson(obj, RequestPressWord::class.java))
        }
    }

    fun isJoinRequest(obj: String): Boolean {
        return obj.contains(REQUEST_JOIN) && !obj.contains(REQUEST_JOINTEAM)
    }
}