package com.adisalagic.codenames.client.api.objects.game



import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
import com.google.gson.annotations.SerializedName

data class TurnTimer(
    @SerializedName("game_timer")
    val gameTimer: ULong,
    @SerializedName("turn_timer")
    val turnTimer: Int,
    @SerializedName("running")
    val running: Boolean
): BaseAPI(Event.GAME_TURN_TIMER)