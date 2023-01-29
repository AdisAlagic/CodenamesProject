package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import java.io.DataInputStream
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class UserHandler(
    client: Socket,
    onMessage: (String) -> Unit,
    onDisconnect: (UserHandler, Exception) -> Unit
) {
    private val logger = Logger.getLogger(this::class)
    private var connected = true
    private val address = client.inetAddress
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private val sendLimit = 1024;
    private val thread = thread {
        var byteBuffer: ByteArray?
        val inStream = client.getInputStream()
        val dataInputStream = DataInputStream(inStream)
        val builder = StringBuilder()
        try {
            client.use {
                while (connected) {
                    do {
                        if (builder.isNotEmpty()) {
                            logger.debug("Got message from ${client.inetAddress}: $builder")
                            onMessage(builder.toString())
                            builder.setLength(0)
                        }
                        if (dataInputStream.available() > 0) {
                            val needToRead = dataInputStream.readInt().flip()
                            byteBuffer = ByteArray(needToRead)
                            val bytes = dataInputStream.read(byteBuffer!!, 0, needToRead)
                            if (bytes != needToRead) {
                                client.close()
                            }
                            builder.append(String(byteBuffer!!, 0, needToRead))
                        }
                    } while (inStream.available() > 0)
                    var sendCounter = 0;
                    synchronized(this) {
                        while (queue.isNotEmpty()) {
                            client.getOutputStream().write(queue.poll())
                            sendCounter++;
                            if (sendCounter > sendLimit) {
                                break
                            }
                        }
                    }
                    Thread.sleep(200)
                }
                client.close()
                onDisconnect(this, DisconnectException())
            }
        }catch (e: IOException){
            onDisconnect(this, e)
        }
    }

    fun getAddress(): InetAddress {
        return address
    }
    fun disconnect(){
        connected = false;
    }

    private fun Int.flip(): Int {
        return ByteBuffer
            .allocate(Int.SIZE_BYTES)
            .order(ByteOrder.BIG_ENDIAN)
            .putInt(this)
            .flip()
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt(0)
    }
}