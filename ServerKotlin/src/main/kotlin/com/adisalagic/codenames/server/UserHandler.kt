package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.utils.generateColor
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class UserHandler(
    client: Socket,
    private val id: Int,
    onMessage: (UserHandler, String) -> Unit,
    private val onDisconnect: (UserHandler, Exception) -> Unit
) {
    private val logger = Logger.getLogger(this::class)
    var justConnected: Boolean = true
        private set(value){
            field = value
        }
    private var connected = true
    private val address = client.inetAddress
    private val port = client.port
    private val queue = ConcurrentLinkedQueue<ByteArray>()
    private val sendLimit = 1024;
    private val thread = thread {
        client.soTimeout = 60
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
                            onMessage(this, builder.toString())
                            if (justConnected){
                                justConnected = false
                            }
                            builder.setLength(0)
                        }
                        try{
                            val needToRead = dataInputStream.readInt().flip()
                            byteBuffer = ByteArray(needToRead)
                            val bytes = dataInputStream.read(byteBuffer!!, 0, needToRead)
                            if (bytes != needToRead) {
                                client.close()
                            }
                            builder.append(String(byteBuffer!!, 0, needToRead))
                        }catch (_: SocketTimeoutException){

                        }catch (eof: EOFException){
                            onDisconnect(this, eof)
                            connected = false
                        }
                    } while (dataInputStream.available() > 0)
                    var sendCounter = 0;
                    synchronized(this) {
                        while (queue.isNotEmpty()) {
                            try {
                                logger.debug("Sending message...")
                                client.getOutputStream().write(queue.poll())
                                logger.debug("Sent!")
                                sendCounter++;
                                if (sendCounter > sendLimit) {
                                    break
                                }
                            }catch (e: Exception){
                                onDisconnect(this, e)
                                connected = false
                            }
                        }
                    }
                }
                client.close()
//                onDisconnect(this, DisconnectException())
            }
        }catch (e: IOException){
            onDisconnect(this, e)
            connected = false

        }
    }

    fun getId(): Int {
        return id
    }

    fun getAddress(): InetAddress {
        return address
    }

    fun getPort(): Int{
        return port
    }
    fun disconnect(){
        logger.info("Disconnecting user $address")
        connected = false;
        onDisconnect(this, DisconnectException())
    }

    fun sendMessage(packetable: Packetable){
        queue.add(packetable.toPaket())
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