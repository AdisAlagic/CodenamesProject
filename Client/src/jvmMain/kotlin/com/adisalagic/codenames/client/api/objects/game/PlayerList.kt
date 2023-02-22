package com.adisalagic.codenames.client.api.objects.game

import PlayerInfo
import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
import com.adisalagic.codenames.client.api.objects.Role

data class PlayerList(
    val users: List<PlayerInfo.User>
) : BaseAPI(Event.GAME_PLAYER_LIST) {
    fun getMasters(): List<PlayerInfo.User> {
        return users.filter { it.role == Role.MASTER }
    }

    fun getHost(): PlayerInfo.User? {
        return users.find { it.isHost }
    }

    fun getPlayers(team: Int): List<PlayerInfo.User> {
        return users.filter { it.role == Role.PLAYER && it.team == team }
    }

    fun getSpectators(): List<PlayerInfo.User> {
        return users.filter { it.role == Role.SPECTATOR }
    }
}