package com.adisalagic.codenames.utils

import com.adisalagic.codenames.server.GameManager
import com.adisalagic.codenames.server.gamelogic.GameState
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.requests.AdminRequest
import com.adisalagic.codenames.server.objects.requests.RequestRestart
import kotlin.random.Random

fun generateColor(id: Int): String {
    val random = Random(id)
    val r = random.nextInt(0, 256)
    val g = random.nextInt(0, 256)
    val b = random.nextInt(0, 256)
    return "#${r.toString(16)}${g.toString(16)}${b.toString(16)}"
}

fun Player.asPlayerListItem(): PlayerList.User {
    return PlayerList.User(
        color = this.color,
        id = this.id,
        isHost = this.isHost,
        nickname = this.nickname,
        role = this.role.toString().lowercase(),
        team = this.team.toString().lowercase()
    )
}

fun AdminRequest.isHost(): Boolean {
    return GameManager.game.isHost(this.user.id)
}

fun GameState.asNetGameState(): com.adisalagic.codenames.server.objects.game.GameState {
    return com.adisalagic.codenames.server.objects.game.GameState(
        blueScore = com.adisalagic.codenames.server.objects.game.GameState.BlueScore(
            this.blueScore.score,
            this.blueScore.team.name.lowercase()
        ),
        redScore = com.adisalagic.codenames.server.objects.game.GameState.RedScore(
            this.redScore.score,
            this.redScore.team.name.lowercase()
        ),
        state = this.state.name.lowercase(),
        turn = com.adisalagic.codenames.server.objects.game.GameState.Turn(
            this.turn.role.name.lowercase(),
            this.turn.team.name.lowercase()
        )
    )
}