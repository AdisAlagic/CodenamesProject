package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.objects.Event
import com.google.gson.annotations.SerializedName

data class RequestRestart(
    @SerializedName("user")
    override val user: Host
): AdminRequest(Event.REQUEST_RESTART, user)