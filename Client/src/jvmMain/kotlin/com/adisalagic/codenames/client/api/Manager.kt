package com.adisalagic.codenames.client.api

import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.api.objects.game.PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.RequestJoin
import com.adisalagic.codenames.client.api.objects.requests.RequestTimer
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import org.apache.logging.log4j.LogManager
import java.net.InetSocketAddress
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.timer

object Manager {
    private val queue = ConcurrentLinkedQueue<String>()
    private val log = LogManager.getLogger("Manager")
    private val eventConverter = EventConverter(
        onGamePlayerList = { eventListener?.onGamePlayerList(it) },
        onGamePlayerInfo = { eventListener?.onGamePlayerInfo(it) },
        onGameState = { eventListener?.onGameState(it) },
        onGameTimer = {
            var tempTime = it.getTime()
            val dateTime = LocalDateTime.parse(it.timeStamp)
            val now = LocalDateTime.now()
            val substruction = Duration.between(dateTime, now).toMillis()
            tempTime = tempTime.plus(substruction.toULong())
            setTimer(tempTime)
            log.debug("Timer received: $tempTime")
        }
    )
    private var connectionListener: ConnectionListener? = null
    private var eventListener: EventListener? = null
    private var timerCounter = 0uL
    private lateinit var timerObject: Timer
    private val socketThread = SocketThread(
        InetSocketAddress("84.2.212.165", 21721),
        onRead = queue::add,
        onDisconnect = {
            connectionListener?.onDisconnect(it)
            ViewModelsStore.mainFrameViewModel.reset()
        },
        onConnectSuccess = {
            connectionListener?.onConnectionSuccess(it)
        }
    )

    init {
        listen()
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

    fun connect() {
        connectionListener?.onConnecting()
        if (!socketThread.isStarted()) {
            socketThread.startListen()
        }
    }

    fun disconnect() {
        socketThread.disconnect()
    }

    private fun startCounting() {
        timerObject = timer(
            name = "In-game timer",
            daemon = false,
            startAt = Date(),
            period = 1L
        ) {
            timerCounter++
        }
    }

    private fun getTimer(): ULong {
        synchronized(timerObject) {
            return timerCounter
        }
    }

    private fun setTimer(time: ULong){
        synchronized(timerObject){
            timerCounter = time
        }
    }

    fun setConnectionListener(connectionListener: ConnectionListener) {
        this.connectionListener = connectionListener
    }

    fun setEventListener(eventListener: EventListener) {
        this.eventListener = eventListener
    }

    private fun listen() {
        Thread {
            while (true) {
                while (queue.isNotEmpty()) {
                    val msg = queue.poll()
                    log.debug("Got message: $msg")
                    eventConverter.provide(msg)
                }
                Thread.sleep(300)
            }
        }.start()
    }

    fun sendMessage(packetable: Packetable) {
        socketThread.sendMessage(packetable)
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
    }

}