package com.adisalagic.codenames.client.api

import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue

class SocketThread(
    address: InetSocketAddress,
    onRead: (String) -> Unit = {},
    onDisconnect: (DisconnectReason) -> Unit = {},
    onConnectSuccess: (address: String) -> Unit = {}
) {
    private var hardDisconnect = true
    private val sendLimit = 1024;
    private val socket = Socket()
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private var thread = recreateThread(address, onRead, onDisconnect, onConnectSuccess)

    fun isStarted(): Boolean = thread.isAlive

    fun startListen() = thread.start()
    fun sendMessage(packetable: Packetable) {
        val obj = packetable.writeAsPacket()
        queue.add(obj)
    }

    fun setAddress(address: String): SocketThread {
        val splitted = address.split(":")
        val port = splitted.getOrElse(1) { "21721" }.toInt()
        thread = recreateThread(InetSocketAddress(splitted.getOrElse(0) { "127.0.0.1" }, port))
        return this
    }

    fun disconnect() {
        //sendMessage() I want to leave
        hardDisconnect = false
        socket.close()
    }

    private fun recreateThread(
        address: InetSocketAddress,
        onRead: (String) -> Unit = {},
        onDisconnect: (DisconnectReason) -> Unit = {},
        onConnectSuccess: (address: String) -> Unit = {}
    ): Thread {
        return Thread {
            try {
                socket.use { socket ->
                    socket.connect(address)
                    onConnectSuccess(socket.inetAddress.hostAddress)
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
                                val needToRead = flipInt(dataInputStream.readInt())
                                buffer = ByteArray(needToRead)
                                val bytes = dataInputStream.read(buffer, 0, needToRead)
                                if (bytes != needToRead) {
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
            } finally {
                thread = recreateThread(address, onRead, onDisconnect, onConnectSuccess)
            }
        }
    }

    private fun flipInt(int: Int): Int {
            return ByteBuffer
                .allocate(Int.SIZE_BYTES)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(int)
                .flip()
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt(0)
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