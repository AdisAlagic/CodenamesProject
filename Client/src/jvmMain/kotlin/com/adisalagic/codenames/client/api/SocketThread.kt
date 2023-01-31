package com.adisalagic.codenames.client.api


import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger

class SocketThread(
    address: InetSocketAddress,
    val onRead: (String) -> Unit = {},
    val onDisconnect: (DisconnectReason) -> Unit = {},
    val onConnectSuccess: (address: String) -> Unit = {}
) {
    private val log = Logger.getLogger(this::class.simpleName)
    private var hardDisconnect = true
    private val sendLimit = 1024;
    private var socket = Socket()
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private var thread = recreateThread(address, onRead, onDisconnect, onConnectSuccess)

    fun isStarted(): Boolean = thread.isAlive

    fun startListen() = thread.start()
    fun sendMessage(packetable: Packetable) {
        val obj = packetable.writeAsPacket()
        queue.add(obj)
    }

    fun setAddress(address: String): SocketThread {
        socket = Socket()
        val splitted = address.split(":")
        val port = splitted.getOrElse(1) { "21721" }.toInt()
        thread = recreateThread(
            InetSocketAddress(splitted.getOrElse(0) { "127.0.0.1" }, port),
            onRead, onDisconnect, onConnectSuccess)
        return this
    }

    fun disconnect() {
        //sendMessage() I want to leave
        hardDisconnect = false
        socket.close()
    }

    private fun recreateThread(
        address: InetSocketAddress,
        onRead: (String) -> Unit,
        onDisconnect: (DisconnectReason) -> Unit,
        onConnectSuccess: (address: String) -> Unit
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
                                log.info { "Available data to read: ${dataInputStream.available()}" }
                                val needToRead = flipInt(dataInputStream.readInt())
                                log.info { "Should read $needToRead bytes" }
                                buffer = ByteArray(needToRead)
                                val bytes = dataInputStream.read(buffer, 0, needToRead)
                                log.info { "Read $bytes bytes" }
                                if (bytes != needToRead) {
                                    log.info { "$bytes != $needToRead; closing socket" }
                                    socket.close()
                                }
                                builder.append(String(buffer, 0, needToRead))
                            }
                        } while (inStream.available() > 0)
                        var sendCounter = 0;
                        synchronized(queue) {
                            while (queue.isNotEmpty()) {
                                log.info { "Sending data" }
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