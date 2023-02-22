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
    private val onRequestSendLog: (RequestSendLog) -> Unit,
    private val onRequestPauseResume: (RequestPauseResume) -> Unit
) {
    private val gson = Gson()

    fun provide(event: Int, obj: String){
        when (event) {
            Event.REQUEST_PRESS_WORD -> onRequestPressWord(gson.fromJson(obj, RequestPressWord::class.java))
            Event.REQUEST_JOIN -> onRequestJoin(gson.fromJson(obj, RequestJoin::class.java))
            Event.REQUEST_JOIN_TEAM -> onRequestJoinTeam(gson.fromJson(obj, RequestJoinTeam::class.java))
            Event.REQUEST_RESTART -> onRequestRestart(gson.fromJson(obj, RequestRestart::class.java))
            Event.REQUEST_SHUFFLE_TEAMS -> onRequestShuffleTeams(gson.fromJson(obj, RequestShuffleTeams::class.java))
            Event.REQUEST_TIMER -> onRequestTimer(gson.fromJson(obj, RequestTimer::class.java))
            Event.REQUEST_SEND_LOG -> onRequestSendLog(gson.fromJson(obj, RequestSendLog::class.java))
            Event.REQUEST_PAUSE_RESUME -> onRequestPauseResume(gson.fromJson(obj, RequestPauseResume::class.java))
        }
    }

    fun isJoinRequest(event: Int): Boolean {
        return Event.REQUEST_JOIN == event
    }
}