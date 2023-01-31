package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import com.google.gson.annotations.SerializedName

data class RequestJoinTeam(
    @SerializedName("request")
    val request: Request
): BaseAPI("request_jointeam") {
    data class Request(
        @SerializedName("user")
        val user: User
    ) {
        data class User(
            @SerializedName("id")
            val id: Int,
            @SerializedName("role")
            val role: String,
            @SerializedName("team")
            val team: String
        )
    }
}