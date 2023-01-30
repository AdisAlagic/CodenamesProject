package com.adisalagic.codenames.server.gamelogic

data class Player(
    val color: String,
    val id: Int,
    val isHost: Boolean,
    val nickname: String,
    val role: Game.Role,
    val team: Game.Team
)