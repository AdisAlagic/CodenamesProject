package com.adisalagic.codenames.client.api

import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger
import kotlin.concurrent.timer

object Manager{
    private val queue = ConcurrentLinkedQueue<String>()
    private val log = Logger.getLogger("Manager")
    private var _listener: Listener = object : Listener{
        override fun onMessageReceive(msg: String) {
            log.info { "Message received: $msg" }
        }

        override fun onConnectionSuccess(address: String) {

        }

        override fun onConnecting() {

        }

        override fun onDisconnect(reason: DisconnectReason) {

        }

    }
    private var timerCounter = 0L
    private lateinit var timerObject: Timer
    private val socketThread = SocketThread(
        InetSocketAddress("84.2.212.165", 21721),
        onRead = queue::add,
        onDisconnect = _listener::onDisconnect,
        onConnectSuccess = _listener::onConnectionSuccess
    )

    init {
        listen()
        startCounting()
    }

    fun connect(address: String, nickname: String) {
        socketThread.setAddress(address)
        connect()
    }
    fun connect(){
        _listener.onConnecting()
        if (!socketThread.isStarted()){
            socketThread.startListen()
        }
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
        synchronized(timerObject){
            return timerCounter
        }
    }

    fun setMessageListener(listener: Listener) {
        _listener = listener
    }

    private fun listen() {
        Thread {
            while (true) {
                synchronized(this) {
                    while (queue.isNotEmpty()) {
                        _listener.onMessageReceive(queue.poll())
                    }
                }
                Thread.sleep(300)
            }
        }.start()
    }

    fun sendMessage(packetable: Packetable) {
        socketThread.sendMessage(packetable)
    }


    interface Listener{
        fun onMessageReceive(msg: String)
        fun onConnectionSuccess(address: String)

        fun onConnecting();
        fun onDisconnect(reason: DisconnectReason)
    }
}