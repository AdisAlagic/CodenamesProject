package com.adisalagic.codenames.client.api.objects.game


import com.adisalagic.codenames.client.api.BaseAPI


data class PlayerList(

    val users: List<User>
) : BaseAPI("game_playerlist") {
    data class User(
        val color: String,
        val id: Int,
        val isHost: Boolean,
        val nickname: String,
        val role: String,
        val team: String
    )
    fun getMasters(): List<User> {
        return users.filter { it.role.equals("master", true) }
    }

    fun getHost(): User? {
        return users.find { it.isHost }
    }

    fun getPlayers(team: String): List<User> {
        return users.filter { it.role.equals("player", true) && it.team.equals(team, true) }
    }

    fun getSpectators(): List<User> {
        return users.filter { it.role.equals("spectator", true) }
    }
}