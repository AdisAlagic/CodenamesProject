package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI


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
}