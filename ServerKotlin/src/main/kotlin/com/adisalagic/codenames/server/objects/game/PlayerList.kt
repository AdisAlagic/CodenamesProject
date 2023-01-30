package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerList(
    @SerialName("users")
    val users: List<User>
):BaseAPI() {
    @Serializable
    data class User(
        @SerialName("color")
        val color: String,
        @SerialName("id")
        val id: Int,
        @SerialName("isHost")
        val isHost: Boolean,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("role")
        val role: String,
        @SerialName("team")
        val team: String
    )
}