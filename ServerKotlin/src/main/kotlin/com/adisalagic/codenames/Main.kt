package com.adisalagic.codenames

import com.adisalagic.codenames.server.ConnectionManager
import com.adisalagic.codenames.server.GameManager
import com.adisalagic.codenames.server.TimerHandler
import com.adisalagic.codenames.server.configuration.ConfigurationManager
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

object Main {
    @JvmStatic
    fun main(args: Array<String>){
        System.setProperty("jansi.passthrough", "true");
        AnsiConsole.systemInstall()
        Runtime.getRuntime().addShutdownHook(Thread {
            Logger.getLogger(this::class).warn("Shutdown hook detected, disconnecting all clients and shutting down server...")
            ConnectionManager.stop()
            TimerHandler.stop()
        })
        ConfigurationManager.loadDefaultConfig()
        GameManager //It is inits only when you call it
        ConnectionManager.start()
    }
}

