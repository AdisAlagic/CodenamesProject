package com.adisalagic.codenames.client.viewmodels

import PlayerInfo
import RequestSendLog
import com.adisalagic.codenames.client.api.Manager
import com.adisalagic.codenames.client.api.objects.Role
import com.adisalagic.codenames.client.api.objects.State
import com.adisalagic.codenames.client.api.objects.Team
import com.adisalagic.codenames.client.api.objects.game.*
import com.adisalagic.codenames.client.api.objects.requests.*
import com.adisalagic.codenames.client.components.Side
import com.adisalagic.codenames.client.utils.CountDownTimer
import com.adisalagic.codenames.client.utils.SoundPlayer
import com.adisalagic.codenames.client.utils.toTeamInt
import com.adisalagic.codenames.client.utils.wholeTeamSkipClicked
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class MainFrameViewModel : ViewModel() {
    private val _state = reset()
    val state = _state.asStateFlow()
    private val wordTimerState = MutableStateFlow(WordTimer())
    val wordState = wordTimerState.asStateFlow()
    val logger: Logger = LogManager.getLogger(this::class.simpleName)
    var wordTimer: CountDownTimer? = null

    init {
        Manager.addTimeListener {
            if (_state.value.gameState?.state == State.STATE_PLAYING){
                wordTimerState.update { it.copy(turnTimer = it.turnTimer.dec()) }
            }
        }
    }

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
            val tempState = _state.value
            wordTimer?.end()?.reset()
            wordTimer = null
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
            if (tempState.gameState?.state == State.STATE_NOT_STARTED && gameState.state == State.STATE_PLAYING){
                SoundPlayer.playSound(SoundPlayer.GAME_START)
            }else if (checkIfLogChanged(gameState)){
                SoundPlayer.playSound(SoundPlayer.GAME_LOG_SEND)
            }
            viewModelScope.launch {
                _state.update { it.copy(gameState = finalState) }
            }
        }

        override fun onGameStartOpenWord(startOpenWord: StartOpenWord) {
            wordTimer?.end()?.reset()
            wordTimer = null
            val words = _state.value.gameState!!.words.toMutableList()
            var word = _state.value.gameState!!.words.find { startOpenWord.word.id == it.id } ?: return
            var index = words.indexOf(word)
            if (index == -1) {
                word = _state.value.gameState!!.words.find { startOpenWord.word.id == it.id } ?: return
                index = words.indexOf(word)
            }
            word = word.copy(
                animationStart = startOpenWord.word.times.first().toULong(),
                animationEnd = startOpenWord.word.times.last().toULong()
            )
            words[index] = word
            val gameState = _state.value.gameState?.copy(words = words)
            _state.update { it.copy(gameState = gameState) }
        }

        override fun onGameTurnTimer(turnTimer: TurnTimer) {
            val time = Manager.getTimer()
            val sub = time - turnTimer.gameTimer
            if (sub >= 1000u) {
                Manager.sendMessage(RequestTimer())
            }
            logger.debug("Client: $time; Server: ${turnTimer.gameTimer}; Sub: $sub")
            wordTimerState.update { it.copy(turnTimer = turnTimer.turnTimer - sub.toInt()) }
        }

        override fun onGameStartSkipWord(startSkipWord: StartSkipWord) {
            val state = _state.value
            if (state.gameState?.skipWord?.wholeTeamSkipClicked(state.gameState.turn.team, state.playerList) == true){
                wordTimer = CountDownTimer(startSkipWord.duration.milliseconds, 1.milliseconds){
                    val millis = it.toDouble(DurationUnit.MILLISECONDS)
                    val result = (millis / startSkipWord.duration).toFloat()
                    logger.debug("Progress bar $result")
                    updateWordTimerProgress(result)
                }.start()
            }else {
                wordTimer?.end()?.reset()
            }
        }
    }

    fun disconnect() {
        Manager.disconnect()
    }

    fun reset(): MutableStateFlow<GameData> {
        if (_state == null){
            return MutableStateFlow(GameData(playerList = PlayerList(emptyList()), myself = null, gameState = null))
        }
        _state.update { it.copy(playerList = PlayerList(emptyList()), myself = null, gameState = null) }
        wordTimerState.update { it.copy(wordProgress = 0F, turnTimer = 0) }
        return _state
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
        wordTimer?.end()?.reset()
        wordTimer = null
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

    fun updateWordTimerProgress(value: Float) {
        wordTimerState.update { it.copy(wordProgress = value) }
    }

    private fun checkIfLogChanged(newState: GameState): Boolean {
        val lastState = _state.value.gameState
        val lastSize = lastState?.redScore?.logs?.size?.let { lastState.blueScore.logs.size.plus(it) } ?: return true
        val newSize = newState.blueScore.logs.size + newState.redScore.logs.size
        return lastSize < newSize
    }

    data class GameData(val playerList: PlayerList, val myself: PlayerInfo?, val gameState: GameState?)
    data class WordTimer(val wordProgress: Float = 0f, val turnTimer: Int = 0)
}