package com.adisalagic.codenames.client.api


import org.apache.logging.log4j.LogManager
import java.io.DataInputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue

class SocketThread(
    address: InetSocketAddress,
    val onRead: (String) -> Unit = {},
    val onDisconnect: (DisconnectReason) -> Unit = {},
    val onConnectSuccess: (address: String) -> Unit = {}
) {
    private val log = LogManager.getLogger(this::class.simpleName)
    private var hardDisconnect = true
    private val sendLimit = 1024
    private var connected = true
    private var socket = createSocket()
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private var thread = recreateThread(address, onRead, onDisconnect, onConnectSuccess)

    fun isStarted(): Boolean = thread.isAlive

    fun startListen() = thread.start()
    fun sendMessage(packetable: Packetable) {
        val obj = packetable.writeAsPacket()
        queue.add(obj)
    }

    fun setAddress(address: String): SocketThread {
        socket = createSocket()
        val splitted = address.split(":")
        val port = splitted.getOrElse(1) { "21721" }.toInt()
        thread = recreateThread(
            InetSocketAddress(splitted.getOrElse(0) { "127.0.0.1" }, port),
            onRead, onDisconnect, onConnectSuccess
        )
        return this
    }

    fun disconnect() {
        //sendMessage() I want to leave
        hardDisconnect = false
        connected = false
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
                    connected = true
                    onConnectSuccess(socket.inetAddress.hostAddress)
                    val inStream = socket.getInputStream()
                    val dataInputStream = DataInputStream(inStream)
                    var buffer: ByteArray = ByteArray(1024)
                    val builder = StringBuilder()
                    while (connected) {
                        do {
                            if (builder.isNotEmpty()) {
                                onRead(builder.toString())
                                builder.setLength(0)
                            }
                            try {
                                val needToRead = flipInt(dataInputStream.readInt())
                                buffer = ByteArray(needToRead)
                                val bytes = dataInputStream.read(buffer, 0, needToRead)
                                if (bytes != needToRead) {
                                    log.debug("$bytes != $needToRead; closing socket")
                                    socket.close()
                                }
                                builder.append(String(buffer, 0, needToRead))
                            } catch (_: SocketTimeoutException) {
                                if (buffer.isNotEmpty()){
                                    buffer.drop(buffer.size)
                                }
                            } catch (e: IOException) {
                                onDisconnect(DisconnectReason.HARD)
                                connected = false
                            }

                        } while (inStream.available() > 0)
                        var sendCounter = 0
                        synchronized(queue) {
                            while (queue.isNotEmpty()) {
                                log.debug("Sending data")
                                try {
                                    socket.getOutputStream().write(queue.poll())
                                } catch (e: IOException) {
                                    onDisconnect(DisconnectReason.HARD)
                                    connected = false
                                }
                                sendCounter++
                                if (sendCounter > sendLimit) {
                                    break
                                }
                            }
                        }
                    }
                }
            } catch (_: SocketTimeoutException) {
                log.debug("Nothing to worry about")
            } catch (exception: IOException) {
                log.error(exception.message)
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
        }.apply {
            this.name = "Socket Thread"
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

    private fun createSocket(): Socket {
        return Socket().apply {
            soTimeout = 60
        }
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