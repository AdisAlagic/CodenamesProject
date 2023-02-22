package com.adisalagic.codenames.utils

import com.adisalagic.codenames.server.GameManager
import com.adisalagic.codenames.server.gamelogic.Game
import com.adisalagic.codenames.server.gamelogic.GameState
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.*
import com.adisalagic.codenames.server.objects.game.PlayerInfo
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.requests.AdminRequest
import com.adisalagic.codenames.server.objects.requests.RequestRestart
import java.nio.CharBuffer
import kotlin.random.Random

fun generateColor(id: Int): Long {
    return Random(id).nextLong(0xFF000000, 0xFFFFFFFF + 1)
}

fun Player.asPlayerInfoItem(): PlayerInfo.User {
    return PlayerInfo.User(
        color = this.color,
        id = this.id,
        isHost = this.isHost,
        nickname = this.nickname,
        role = this.role.toRoleInt(),
        team = this.team.toTeamInt()
    )
}

fun AdminRequest.isHost(): Boolean {
    return GameManager.game.isHost(this.user.id)
}

fun createDictionaryWords(words: List<CharSequence>): List<GameState.Word> {
    val list = mutableListOf<GameState.Word>()
    val copy = mutableListOf<CharSequence>().apply { addAll(words) }
    var remainingRed = 17
    var remainingBlue = 17
    var remainingBlack = 1
    repeat(36) {
        val random = copy.removeAt(Random.nextInt(0, copy.size))
        list.add(
            GameState.Word(
                id = it,
                name = random.toString(),
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
        blueScore = com.adisalagic.codenames.server.objects.game.GameState.Score(
            this.blueScore.score,
            this.blueScore.team.toTeamInt(),
            this.blueScore.logs
        ),
        redScore = com.adisalagic.codenames.server.objects.game.GameState.Score(
            this.redScore.score,
            this.redScore.team.toTeamInt(),
            this.redScore.logs
        ),
        state = this.state.toStateInt(),
        turn = com.adisalagic.codenames.server.objects.game.GameState.Turn(
            this.turn.role.toRoleInt(),
            this.turn.team.toTeamInt()
        ),
        words = this.words.map { it.asNetWord() }
    )
}

fun GameState.Word.asNetWord(): com.adisalagic.codenames.server.objects.game.GameState.Word {
    return com.adisalagic.codenames.server.objects.game.GameState.Word(
        id = this.id,
        name = this.name,
        side = this.side.toSideInt(),
        visible = this.visible,
        usersPressed = this.usersPressed.map {
            return@map it.asPlayerInfoItem()
        }
    )
}

fun shouldPlayerBecomeMaster(
    isMaster: Boolean,
    side: Game.Team,
    blueHasMaster: Boolean,
    redHasMaster: Boolean
): Boolean {
    if (!isMaster) {
        return false
    }
    return when (side) {
        Game.Team.RED -> !(redHasMaster && isMaster)
        Game.Team.BLUE -> !(blueHasMaster && isMaster)
        Game.Team.NONE -> false
    }
}

fun Game.Role.toRoleInt(): Int {
    return when (this) {
        Game.Role.SPECTATOR -> Role.SPECTATOR
        Game.Role.MASTER -> Role.MASTER
        Game.Role.PLAYER -> Role.PLAYER
    }
}

fun Game.Team.toTeamInt(): Int {
    return when (this) {
        Game.Team.RED -> Team.RED
        Game.Team.BLUE -> Team.BLUE
        Game.Team.NONE -> Team.NONE
    }
}

fun GameState.Side.toSideInt(): Int {
    return when (this) {
        GameState.Side.BLUE -> Side.BLUE
        GameState.Side.RED -> Side.RED
        GameState.Side.WHITE -> Side.WHITE
        GameState.Side.BLACK -> Side.BLACK
    }
}

fun GameState.GeneralState.toStateInt(): Int {
    return when (this) {
        GameState.GeneralState.NOT_STARTED -> State.STATE_NOT_STARTED
        GameState.GeneralState.PLAYING -> State.STATE_PLAYING
        GameState.GeneralState.PAUSED -> State.STATE_PAUSED
        GameState.GeneralState.ENDED -> State.STATE_ENDED
    }
}

fun Int.toRole(): Game.Role {
    return when (this) {
        Role.MASTER -> Game.Role.MASTER
        Role.PLAYER -> Game.Role.PLAYER
        Role.SPECTATOR -> Game.Role.SPECTATOR
        else -> {
            Game.Role.SPECTATOR
        }
    }
}

fun Int.toTeam(): Game.Team {
    return when (this) {
        Team.RED -> Game.Team.RED
        Team.BLUE -> Game.Team.BLUE
        Team.NONE -> Game.Team.NONE
        else -> { Game.Team.NONE }
    }
}
