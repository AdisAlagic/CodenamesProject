package com.adisalagic.codenames

import com.adisalagic.codenames.server.ConnectionManager

object Main {
    @JvmStatic
    fun main(args: Array<String>){
        Runtime.getRuntime().addShutdownHook(Thread{
            Logger.getLogger(this::class).warn("Shutdown hook detected, disconnecting all clients and shutting down server...")
            ConnectionManager.stop()
        })
        ConnectionManager.start()
    }
}

