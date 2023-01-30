package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestJoin(
    @SerialName("user")
    val user: User
): BaseAPI() {
    @Serializable
    data class User(
        @SerialName("nickname")
        val nickname: String
    )
}