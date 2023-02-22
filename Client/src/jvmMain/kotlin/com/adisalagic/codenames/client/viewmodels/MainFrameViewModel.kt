package com.adisalagic.codenames.client.viewmodels

import PlayerInfo
import RequestSendLog
import com.adisalagic.codenames.client.api.Manager
import com.adisalagic.codenames.client.api.objects.Role
import com.adisalagic.codenames.client.api.objects.Team
import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.game.StartOpenWord
import com.adisalagic.codenames.client.api.objects.requests.*
import com.adisalagic.codenames.client.components.Side
import com.adisalagic.codenames.client.utils.toIntSide
import com.adisalagic.codenames.client.utils.toTeamInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

class MainFrameViewModel : ViewModel() {
    private val _state = reset()
    val state = _state.asStateFlow()
    val logger: Logger = LogManager.getLogger(this::class.simpleName)
    var wordTimer: Timer? = null


    private val eventListener = object : Manager.EventListener {
        override fun onGamePlayerList(gamePlayerList: PlayerList) {
            viewModelScope.launch {
                _state.update { it.copy(playerList = gamePlayerList) }
                logger.debug("Data updated")
                val me = gamePlayerList.users.find { _state.value.myself?.user?.id == it.id }
                if (me != null) {
                    _state.update {
                        it.copy(
                            myself = PlayerInfo(
                                PlayerInfo.User(
                                    color = me.color,
                                    id = me.id,
                                    isHost = me.isHost,
                                    nickname = me.nickname,
                                    role = me.role,
                                    team = me.team
                                )
                            )
                        )
                    }
                }
            }
        }


        override fun onGamePlayerInfo(playerInfo: PlayerInfo) {
            viewModelScope.launch {
                _state.update { it.copy(myself = playerInfo) }
            }
        }

        override fun onGameState(gameState: GameState) {
            var words = emptyList<GameState.Word>()
            if (_state.value.gameState != null) {
                words = _state.value.gameState!!.words
            }
            val tempWords = gameState.words.toMutableList()
            if (words.isNotEmpty()) {
                tempWords.forEachIndexed { index, word ->
                    val cpyWord = words[index]
                    tempWords[index] =
                        word.copy(animationStart = cpyWord.animationStart, animationEnd = cpyWord.animationEnd)
                }
            }
            val finalState = gameState.copy(words = tempWords)
            if (wordTimer != null){
                wordTimer?.cancel()
                wordTimer = null
            }
            viewModelScope.launch {
                _state.update { it.copy(gameState = finalState) }
            }
        }

        override fun onGameStartOpenWord(startOpenWord: StartOpenWord) {
            var word = _state.value.gameState!!.words.find { startOpenWord.word.id == it.id } ?: return
            val words = _state.value.gameState!!.words.toMutableList()
            val index = words.indexOf(word)
            word = word.copy(
                animationStart = startOpenWord.word.times.first().toULong(),
                animationEnd = startOpenWord.word.times.last().toULong()
            )
            words[index] = word
            val gameState = _state.value.gameState?.copy(words = words)
            _state.update { it.copy(gameState = gameState) }
        }
    }

    fun disconnect() {
        Manager.disconnect()
    }

    fun reset(): MutableStateFlow<GameData> {
        return MutableStateFlow(GameData(PlayerList(emptyList()), null, null))
    }

    init {
        Manager.setEventListener(eventListener)
    }

    fun sendBecomeMasterRequest(side: Side) {
        sendRequestJoinTeam(Role.MASTER, side)
    }

    fun sendBecomePlayerRequest(side: Side) {
        sendRequestJoinTeam(Role.PLAYER, side)
    }

    fun sendBecomeSpectatorRequest() {
        sendRequestJoinTeam(Role.SPECTATOR, Side.WHITE)
    }

    fun sendRequestShuffleTeams() {
        if (_state.value.myself == null) {
            return
        }
        val me = _state.value.myself
        Manager.sendMessage(RequestShuffleTeams(AdminRequest.Host(me!!.user.id)))
    }

    fun sendRestartRequest() {
        if (_state.value.myself == null) {
            return
        }
        val me = _state.value.myself
        Manager.sendMessage(RequestRestart(AdminRequest.Host(me!!.user.id)))
    }

    fun sendPauseResumeRequest() {
        if (_state.value.myself == null) {
            return
        }
        val me = _state.value.myself
        Manager.sendMessage(RequestPauseResume(AdminRequest.Host(me!!.user.id)))
    }

    fun sendWordPressRequest(wordId: Int, pressed: Boolean) {
        Manager.sendMessage(
            RequestPressWord(
                RequestPressWord.User(_state.value.myself!!.user.id),
                RequestPressWord.Word(wordId, pressed)
            )
        )
    }

    fun sendLogRequest(log: String) {
        if (log.isBlank()) {
            return
        }
        Manager.sendMessage(
            RequestSendLog(
                log = log,
                RequestPressWord.User(_state.value.myself!!.user.id)
            )
        )
    }

    private fun sendRequestJoinTeam(role: Int, side: Side) {
        logger.debug("Creating request for side or team change")
        val me = _state.value.myself
        if (me == null) {
            Manager.disconnect()
        }
        var team = side.toTeamInt()
        if (side == Side.WHITE) {
            team = Team.NONE
        }
        Manager.sendMessage(
            RequestJoinTeam(
                RequestJoinTeam.Request(
                    RequestJoinTeam.Request.User(
                        id = me!!.user.id,
                        role = role,
                        team = team
                    )
                )
            )
        )
    }

    fun deleteAnimationTime(word: GameState.Word) {
        val words = _state.value.gameState!!.words.toMutableList().apply {
            val index = indexOf(word)
            if (index == -1) {
                return@apply
            }
            this[index] = this[index].copy(animationStart = null, animationEnd = null)
        }
        viewModelScope.launch {
            _state.update { it.copy(gameState = it.gameState?.copy(words = words)) }
        }
    }

    data class GameData(val playerList: PlayerList, val myself: PlayerInfo?, val gameState: GameState?)
}