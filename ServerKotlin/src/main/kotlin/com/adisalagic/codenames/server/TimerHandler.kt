package com.adisalagic.codenames.server

import kotlin.concurrent.timer
import kotlin.concurrent.timerTask

object TimerHandler {
    private var timerValue = 0uL
    private var threadTimer = timer(
        name = "In-game timer",
        period = 1
    ) {
        timerValue++
    }

    fun getTimer(): ULong {
        synchronized(this) {
            return timerValue
        }
    }

    fun stop() {
        threadTimer.cancel()
        threadTimer.purge()
    }

    fun resume() {
        threadTimer = timer(
            name = "In-game timer",
            period = 1
        ) {
            timerValue++
        }
    }
}