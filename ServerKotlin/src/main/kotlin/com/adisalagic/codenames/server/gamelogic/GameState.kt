package com.adisalagic.codenames.server.gamelogic

data class GameState(
    val state: GeneralState,
    val turn: Turn,
    val blueScore: Score,
    val redScore: Score
) {
    enum class GeneralState {
        NOT_STARTED,
        PLAYING,
        PAUSED,
        ENDED
    }

    data class Turn(val team: Game.Team, val role: Game.Role)

    data class Score(val team: Game.Team, val score: Int)

    companion object {
        private const val startScore = 17

        fun reset(): GameState {
            return GameState(
                state = GeneralState.NOT_STARTED,
                turn = Turn(Game.Team.getRandom(), Game.Role.MASTER),
                blueScore = Score(Game.Team.BLUE, startScore),
                redScore = Score(Game.Team.RED, startScore)
            )
        }
    }

}