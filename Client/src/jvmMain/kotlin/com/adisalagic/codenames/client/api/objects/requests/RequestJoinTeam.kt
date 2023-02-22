package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
import com.google.gson.annotations.SerializedName

data class RequestJoinTeam(
    @SerializedName("request")
    val request: Request
): BaseAPI(Event.REQUEST_JOIN_TEAM) {
    data class Request(
        @SerializedName("user")
        val user: User
    ) {
        data class User(
            @SerializedName("id")
            val id: Int,
            @SerializedName("role")
            val role: Int,
            @SerializedName("team")
            val team: Int
        )
    }
}