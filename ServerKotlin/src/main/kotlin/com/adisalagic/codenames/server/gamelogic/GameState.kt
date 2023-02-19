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

    data class Turn(val team: Game.Team, val role: Game.Role){
        companion object {
            val BlueMaster = Turn(Game.Team.BLUE, Game.Role.MASTER)
            val RedMaster = Turn(Game.Team.RED, Game.Role.MASTER)
            val BluePlayers = Turn(Game.Team.BLUE, Game.Role.PLAYER)
            val RedPlayers = Turn(Game.Team.RED, Game.Role.PLAYER)
        }
    }

    data class Score(val team: Game.Team, val score: Int, val logs: List<String>)

    companion object {
        private const val startScore = 17

        fun reset(): GameState {
            return GameState(
                state = GeneralState.NOT_STARTED,
                turn = Turn(Game.Team.getRandom(), Game.Role.MASTER),
                blueScore = Score(Game.Team.BLUE, startScore, emptyList()),
                redScore = Score(Game.Team.RED, startScore, emptyList()),
                words = createDictionaryWords(ConfigurationManager.dictionary)
            )
        }
    }

}