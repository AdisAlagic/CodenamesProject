package com.adisalagic.codenames.client.api

import PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.*
import com.adisalagic.codenames.client.api.objects.requests.RequestJoin
import com.adisalagic.codenames.client.api.objects.requests.RequestTimer
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import org.apache.logging.log4j.LogManager
import java.net.InetSocketAddress
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

object Manager {
    private val queue = ConcurrentLinkedQueue<Pair<Int, String>>()
    private val log = LogManager.getLogger("Manager")
    private val timerQueue = ConcurrentLinkedQueue<ULong>()
    private val idsToDeleteQueue = ConcurrentLinkedQueue<Int>()
    private var queueToAddListeners = ConcurrentLinkedQueue<EventTimer>()
    private var timerListeners: MutableList<EventTimer> = ArrayList()
    private val eventConverter = EventConverter(
        onGamePlayerList = { eventListener?.onGamePlayerList(it) },
        onGamePlayerInfo = { eventListener?.onGamePlayerInfo(it) },
        onGameState = { eventListener?.onGameState(it) },
        onGameTimer = {
            var tempTime = it.getTime()
            val dateTime = LocalDateTime.parse(it.timeStamp)
            val now = LocalDateTime.now()
            val subtraction = Duration.between(dateTime, now).toMillis()
            tempTime = tempTime.plus(subtraction.toULong())
            setTimer(tempTime)
            log.debug("Timer received: $tempTime")
        },
        onGameStartOpenWord = { eventListener?.onGameStartOpenWord(it) },
        onGameTurnTimer = { eventListener?.onGameTurnTimer(it) },
        onGameStartSkipWord = { eventListener?.onGameStartSkipWord(it) }
    )
    private var connectionListener: ConnectionListener? = null
    private var eventListener: EventListener? = null
    private var timerCounter = 0uL
    private lateinit var timerObject: Timer
    private val socketThread = SocketThread(
        InetSocketAddress("127.0.0.1", 21721),
        onRead = { event, msg -> queue.add(event to msg) },
        onDisconnect = {
            connectionListener?.onDisconnect(it)
            ViewModelsStore.mainFrameViewModel.reset()
        },
        onConnectSuccess = {
            connectionListener?.onConnectionSuccess(it)
        }
    )

    init {
        startCounting()
    }

    fun connect(address: String, nickname: String) {
        socketThread.setAddress(address)
        connect()
        socketThread.sendMessage(
            RequestJoin(RequestJoin.User(nickname))
        )
        sendMessage(RequestTimer())
    }

    private fun connect() {
        connectionListener?.onConnecting()
        if (!socketThread.isStarted()) {
            socketThread.startListen()
        }
    }

    fun disconnect() {
        socketThread.disconnect()
    }

    private fun startCounting() {
        var nextTime = ULong.MAX_VALUE;
        timerObject = fixedRateTimer(
            name = "In-game timer",
            daemon = false,
            startAt = Date(),
            period = 1L
        ) {
            timerCounter++
            while (idsToDeleteQueue.isNotEmpty()){
                timerListeners.removeAt(idsToDeleteQueue.poll())
            }
            while (queueToAddListeners.isNotEmpty()) {
                timerListeners.add(queueToAddListeners.poll())
            }
            timerListeners.forEach { it.onTime(timerCounter) }
            while (queue.isNotEmpty()) {
                val msg = queue.poll()
                log.debug("Got message: {}", msg)
                eventConverter.provide(msg.first, msg.second)
            }
            if (timerCounter >= nextTime) {
                nextTime = ULong.MAX_VALUE
            }
            if (timerQueue.isNotEmpty()) {
                nextTime = timerQueue.poll()
            }
        }
        val delay = 2.minutes.toLong(DurationUnit.MILLISECONDS)
        timerObject.schedule(
            timerTask {
                sendMessage(RequestTimer())
            },
            delay,
            delay
        )
    }

    fun getTimer(): ULong {
        synchronized(timerObject) {
            return timerCounter
        }
    }

    private fun setTimer(time: ULong) {
        synchronized(timerObject) {
            timerCounter = time
        }
    }

    fun setConnectionListener(connectionListener: ConnectionListener) {
        this.connectionListener = connectionListener
    }

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    fun sendMessage(packetable: Packetable) {
        socketThread.sendMessage(packetable)
    }

    fun addTimeListener(eventTimer: EventTimer) {
        queueToAddListeners.add(eventTimer)
    }

    fun removeTimeListener(eventTimer: EventTimer) {
        val id = timerListeners.indexOf(eventTimer)
        if (id != -1) {
            idsToDeleteQueue.add(id)
        }
    }

    fun interface EventTimer {
        fun onTime(time: ULong)
    }

    interface ConnectionListener {
        fun onConnectionSuccess(address: String)

        fun onConnecting()
        fun onDisconnect(reason: DisconnectReason)
    }

    interface EventListener {

        fun onGamePlayerList(gamePlayerList: PlayerList)

        fun onGamePlayerInfo(playerInfo: PlayerInfo)
        fun onGameState(gameState: GameState)
        fun onGameStartOpenWord(startOpenWord: StartOpenWord)
        fun onGameTurnTimer(turnTimer: TurnTimer)

        fun onGameStartSkipWord(startSkipWord: StartSkipWord)
    }

}