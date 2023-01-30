package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import java.net.ServerSocket
import java.net.Socket

class ServerThread(
    port: Int = 21721,
    onAnyError: (Exception) -> Unit,
    onUserConnected: (Socket) -> Unit
) {
    private val logger = Logger.getLogger(this::class)

    private var stop: Boolean = false
    private var started = false
    private val thread = Thread {
        started = true
        logger.info("Starting server on port: $port")
        val serverSocket = ServerSocket(port)
        logger.info("Starting server success!")
        while (!stop){
            val client = serverSocket.accept()
            logger.info("Accepted client ${client.inetAddress}:${client.port}")
            onUserConnected(client)
            Thread.sleep(200)
        }
    }

    fun stop(){
        this.stop = true
    }

    fun start(){
        if (!started){
            thread.start()
        }
    }
}