package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.configuration.ConfigurationManager
import java.net.ServerSocket
import java.net.Socket

class ServerThread(
    port: Int = ConfigurationManager.config.port,
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