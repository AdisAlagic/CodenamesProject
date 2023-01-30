package com.adisalagic.codenames.utils

import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.game.PlayerList
import kotlin.random.Random

fun generateColor(id: Int): String {
    val random = Random(id)
    val r = random.nextInt(0, 256)
    val g = random.nextInt(0, 256)
    val b = random.nextInt(0, 256)
    return "#${r.toString(16)}${g.toString(16)}${b.toString(16)}"
}

fun Player.asPlayerListItem(): PlayerList.User{
    return PlayerList.User(
        color = this.color,
        id = this.id,
        isHost = this.isHost,
        nickname = this.nickname,
        role = this.role.toString().lowercase(),
        team = this.team.toString().lowercase()
    )
}