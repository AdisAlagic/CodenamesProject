package com.adisalagic.codenames

import com.adisalagic.codenames.server.ConnectionManager
import com.adisalagic.codenames.server.configuration.ConfigurationManager

object Main {
    @JvmStatic
    fun main(args: Array<String>){
        Runtime.getRuntime().addShutdownHook(Thread{
            Logger.getLogger(this::class).warn("Shutdown hook detected, disconnecting all clients and shutting down server...")
            ConnectionManager.stop()
        })
        ConfigurationManager.loadDefaultConfig()
        ConnectionManager.start()
    }
}

