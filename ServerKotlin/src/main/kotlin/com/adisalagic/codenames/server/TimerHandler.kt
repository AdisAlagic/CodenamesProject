package com.adisalagic.codenames.server

import java.util.TimerTask
import kotlin.concurrent.timer

object TimerHandler {
    private var timerValue = 0uL
    private var threadTimer = timer(
        name = "In-game timer",
        period = 1
    ){
        timerValue++
    }

    fun getTimer(): ULong{
        synchronized(this){
            return timerValue
        }
    }

    fun stop(){
        threadTimer.cancel()
        threadTimer.purge()
    }
}