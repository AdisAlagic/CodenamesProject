package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event

data class TimerInfo(val timer: ULong, val timeStamp: CharSequence): BaseAPI(Event.GAME_TIMER) {
    fun getTime(): ULong {
        return try {
            timer
        } catch (e: Exception) {
            0uL
        }
    }
}
