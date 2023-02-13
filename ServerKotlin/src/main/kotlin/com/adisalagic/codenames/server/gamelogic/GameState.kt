package com.adisalagic.codenames.server.gamelogic

import com.adisalagic.codenames.server.configuration.ConfigurationManager
import com.adisalagic.codenames.utils.createDictionaryWords

data class GameState(
    val state: GeneralState,
    val turn: Turn,
    val blueScore: Score,
    val redScore: Score,
    val words: List<Word>
) {
    enum class GeneralState {
        NOT_STARTED,
        PLAYING,
        PAUSED,
        ENDED
    }

    enum class Side{
        BLUE,
        RED,
        WHITE,
        BLACK;

        companion object {
            val values = Side.values()
        }
    }

    data class Word(val id: Int, val name: String, val side: Side, val visible: Boolean, val usersPressed: List<Player>)

    data class Turn(val team: Game.Team, val role: Game.Role)

    data class Score(val team: Game.Team, val score: Int)

    companion object {
        private const val startScore = 17

        fun reset(): GameState {
            return GameState(
                state = GeneralState.NOT_STARTED,
                turn = Turn(Game.Team.getRandom(), Game.Role.MASTER),
                blueScore = Score(Game.Team.BLUE, startScore),
                redScore = Score(Game.Team.RED, startScore),
                words = createDictionaryWords(ConfigurationManager.dictionary)
            )
        }
    }

}