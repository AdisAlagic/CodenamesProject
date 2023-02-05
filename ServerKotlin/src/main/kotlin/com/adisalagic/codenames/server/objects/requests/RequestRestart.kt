package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.GameManager
import com.adisalagic.codenames.server.gamelogic.Game
import com.google.gson.annotations.SerializedName

data class RequestRestart(
    @SerializedName("user")
    override val user: Host
): AdminRequest("request_restart", user)