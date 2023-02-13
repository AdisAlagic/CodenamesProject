package com.adisalagic.codenames.utils

import com.adisalagic.codenames.server.GameManager
import com.adisalagic.codenames.server.gamelogic.Game
import com.adisalagic.codenames.server.gamelogic.GameState
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.game.PlayerInfo
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

fun Player.asPlayerInfoItem(): PlayerInfo.User {
    return PlayerInfo.User(
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

fun createDictionaryWords(words: List<String>): List<GameState.Word> {
    val list = mutableListOf<GameState.Word>()
    val copy = mutableListOf<String>().apply { addAll(words) }
    var remainingRed = 17
    var remainingBlue = 17
    var remainingBlack = 1
    repeat(36) {
        val random = copy.removeAt(Random.nextInt(0, copy.size))
        list.add(
            GameState.Word(
                id = it,
                name = random,
                visible = false,
                side = if (remainingRed > 0) {
                    remainingRed--
                    GameState.Side.RED
                } else if (remainingBlue > 0) {
                    remainingBlue--
                    GameState.Side.BLUE
                } else if (remainingBlack > 0) {
                    remainingBlack--
                    GameState.Side.BLACK
                } else {
                    GameState.Side.WHITE
                },
                usersPressed = emptyList()
            )
        )
    }
    return list.apply { shuffle() }
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
        ),
        words = this.words.map { it.asNetWord() }
    )
}

fun GameState.Word.asNetWord(): com.adisalagic.codenames.server.objects.game.GameState.Word {
    return com.adisalagic.codenames.server.objects.game.GameState.Word(
        id = this.id,
        name = this.name,
        side = this.side.name.lowercase(),
        visible = this.visible,
        usersPressed = this.usersPressed.map {
            return@map it.asPlayerInfoItem()
        }
    )
}

fun shouldPlayerBecomeMaster(isMaster: Boolean, side: Game.Team, blueHasMaster: Boolean, redHasMaster: Boolean): Boolean {
    if (!isMaster){ return false }
    return when(side){
        Game.Team.RED -> !(redHasMaster && isMaster)
        Game.Team.BLUE -> !(blueHasMaster && isMaster)
        Game.Team.NONE -> false
    }
}