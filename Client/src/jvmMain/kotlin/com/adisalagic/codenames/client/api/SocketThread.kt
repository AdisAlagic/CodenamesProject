package com.adisalagic.codenames.client.api

import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors

class SocketThread(
    address: InetSocketAddress,
    onRead: (String) -> Unit = {},
    onDisconnect: (DisconnectReason) -> Unit = {},
) {
    private var hardDisconnect = true
    private val sendLimit = 1024;
    private val socket = Socket()
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private val thread = Thread {
        try {
            socket.use { socket ->
                socket.connect(address)
                val inStream = socket.getInputStream()
                val dataInputStream = DataInputStream(inStream)
                var buffer: ByteArray?
                val builder = StringBuilder()
                while (!socket.isClosed) {
                    do {
                        if (builder.isNotEmpty()) {
                            onRead(builder.toString())
                            builder.setLength(0)
                        }
                        if (dataInputStream.available() > 0) {
                            val needToRead = dataInputStream.readInt()
                            buffer = ByteArray(needToRead);
                            val bytes = dataInputStream.read(buffer, 0, needToRead)
                            if (bytes != needToRead){
                                socket.close()
                            }
                            builder.append(String(buffer, 0, needToRead))
                        }
                    } while (inStream.available() > 0)
                    var sendCounter = 0;
                    synchronized(queue) {
                        while (queue.isNotEmpty()) {
                            socket.getOutputStream().write(queue.poll())
                            sendCounter++;
                            if (sendCounter > sendLimit) {
                                break
                            }
                        }
                    }
                    Thread.sleep(200)
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
        queue.add(obj)
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