package com.adisalagic.codenames.client.api

import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

object Manager {
    private val queue = ConcurrentLinkedQueue<String>()
    private var _listener: (msg: String) -> Unit = {}
    private val socketThread = SocketThread(
        InetSocketAddress("127.0.0.1", 8000),
        onRead = queue::add
    )

    init {
        listen()
    }

    fun setMessageListener(listener: (msg: String) -> Unit) {
        _listener = listener
    }

    private fun listen() {
        Thread {
            while(true){
                synchronized(queue){
                    while (queue.isNotEmpty()){
                        _listener(queue.poll())
                    }
                }
                Thread.sleep(300)
            }
        }
    }

    fun sendMessage(packetable: Packetable) {
        socketThread.sendMessage(packetable)
    }

}