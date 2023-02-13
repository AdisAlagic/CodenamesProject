package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI

data class TimerInfo(val timer: String, val timeStamp: String) : BaseAPI("game_timer") {

    fun getTime(): ULong {
        return try {
            timer.toULong()
        } catch (e: Exception) {
            0uL
        }
    }
}