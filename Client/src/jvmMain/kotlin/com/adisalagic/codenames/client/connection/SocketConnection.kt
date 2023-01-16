package com.adisalagic.codenames.client.connection

import java.io.BufferedInputStream
import java.lang.StringBuilder
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors
import kotlin.text.StringBuilder

class SocketConnection(private val configuration: Configuration) {
    private val clientSocket = Socket(configuration.address, configuration.port)
    private val thread = Thread() { handleConnection() }
    private val executor = Executors.newCachedThreadPool()


    private fun handleConnection() {
        clientSocket.use {
            it.connect(InetSocketAddress(configuration.address, configuration.port))
        }
        var bufferRead: Int;
        val buffer = ByteArray(1024)
        val string = StringBuilder()
        while (clientSocket.isConnected){
            bufferRead = clientSocket.getInputStream().read(buffer)
            string.append(buffer)
        }
    }

    fun sendMessage(byteArray: ByteArray) {
        executor.execute {
            clientSocket.use {
                it.getOutputStream().write(byteArray)
            }
        }
    }

    data class Configuration(
        val address: Inet4Address = Inet4Address.getLocalHost() as Inet4Address,
        val port: Int = 21721,
        val nick: String
    )
}



