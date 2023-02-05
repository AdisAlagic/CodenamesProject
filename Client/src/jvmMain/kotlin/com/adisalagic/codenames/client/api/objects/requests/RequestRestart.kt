package com.adisalagic.codenames.client.api.objects.requests


import com.google.gson.annotations.SerializedName

data class RequestRestart(
    @SerializedName("user")
    @Transient override val user: Host
) : AdminRequest("request_restart", user)