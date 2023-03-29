package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.configuration.ConfigurationManager
import java.net.InetSocketAddress
import java.net.InterfaceAddress
import java.net.ServerSocket
import java.net.Socket

class ServerThread(
    port: Int = ConfigurationManager.config.port,
    private var address: String = ConfigurationManager.config.ip,
    onAnyError: (Exception) -> Unit,
    onUserConnected: (Socket) -> Unit
) {
    private val logger = Logger.getLogger(this::class)

    private var stop: Boolean = false
    private var started = false
    private val thread = Thread {
        started = true
        if (address.isBlank()) {
            address = "127.0.0.1"
        }
        logger.info("Starting server on port: $port and address: $address")
        val serverSocket = ServerSocket().apply {
            bind(InetSocketAddress(address, port))
        }
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