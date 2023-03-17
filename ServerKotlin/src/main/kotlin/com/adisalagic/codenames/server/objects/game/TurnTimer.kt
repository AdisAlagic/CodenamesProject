package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event
import com.google.gson.annotations.SerializedName

data class TurnTimer(
    @SerializedName("game_timer")
    val gameTimer: ULong,
    @SerializedName("turn_timer")
    val turnTimer: Int,
    @SerializedName("running")
    val running: Boolean
): BaseAPI(Event.GAME_TURN_TIMER)