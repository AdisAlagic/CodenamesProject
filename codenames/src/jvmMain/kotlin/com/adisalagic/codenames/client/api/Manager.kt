package com.adisalagic.codenames.client.api

import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

object Manager {
    private val queue = ConcurrentLinkedQueue<String>()
    private val socketThread = SocketThread(
        InetSocketAddress("127.0.0.1", 8000),
        onRead = queue::add
    )
}