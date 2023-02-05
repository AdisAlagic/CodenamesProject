package com.adisalagic.codenames.client.api

import com.adisalagic.codenames.client.api.objects.game.GameState
import com.adisalagic.codenames.client.api.objects.game.PlayerInfo
import com.adisalagic.codenames.client.api.objects.game.PlayerList
import com.adisalagic.codenames.client.api.objects.requests.RequestJoin
import com.adisalagic.codenames.client.viewmodels.ViewModelsStore
import org.apache.logging.log4j.LogManager
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger
import kotlin.concurrent.timer

object Manager {
    private val queue = ConcurrentLinkedQueue<String>()
    private val log = LogManager.getLogger("Manager")
    private val eventConverter = EventConverter(
        onGamePlayerList = { eventListener?.onGamePlayerList(it) },
        onGamePlayerInfo = { eventListener?.onGamePlayerInfo(it) },
        onGameState = { eventListener?.onGameState(it) }
    )
    private var connectionListener: ConnectionListener? = null
    private var eventListener: EventListener? = null
    private var timerCounter = 0L
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

    fun getTimer(): Long {
        synchronized(timerObject) {
            return timerCounter
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