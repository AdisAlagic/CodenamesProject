package com.adisalagic.codenames.client.api

import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

class SocketThread(
    address: InetSocketAddress,
    onRead: (String) -> Unit = {},
    onDisconnect: (DisconnectReason) -> Unit = {},
) {
    private var hardDisconnect = true
    private val socket = Socket()
    private val thread = Thread() {
        try {
            socket.use { socket ->
                socket.connect(address)
                val inStream = socket.getInputStream()
                val dataInputStream = DataInputStream(inStream)
                var buffer: ByteArray? = null
                val builder = StringBuilder()
                while (!socket.isClosed) {
                    do {
                        if (builder.isNotEmpty()) {
                            onRead(builder.toString())
                            builder.setLength(0)
                        }
                        val needToRead = dataInputStream.readInt()
                        buffer = dataInputStream.readNBytes(needToRead)
                        builder.append(String(buffer, 0, needToRead))
                    } while (inStream.available() > 0)
                }
            }
        } catch (exception: Exception) {
            onDisconnect(
                if (hardDisconnect) {
                    DisconnectReason.HARD
                } else {
                    DisconnectReason.SOFT
                }
            )
        }
    }

    fun startListen() = thread.start()

    fun sendMessage(packetable: Packetable) {
        val obj = packetable.writeAsPacket()
        Executors.newCachedThreadPool().execute { socket.getOutputStream().write(obj) }
    }

    fun disconnect() {
        //sendMessage() I want to leave
        hardDisconnect = false
        socket.close()
    }
}

enum class DisconnectReason {
    /**
     * Any exception, that closes socket
     */
    HARD,

    /**
     * User voluntarily disconnected
     */
    SOFT
}