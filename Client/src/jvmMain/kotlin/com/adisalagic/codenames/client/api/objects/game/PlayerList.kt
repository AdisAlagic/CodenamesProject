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
}