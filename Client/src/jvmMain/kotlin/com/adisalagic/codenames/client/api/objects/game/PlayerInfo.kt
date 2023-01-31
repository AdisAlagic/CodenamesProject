package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI
import com.google.gson.annotations.SerializedName

data class PlayerInfo(
    @SerializedName("user")
    val user: User
): BaseAPI("game_playerinfo") {
    data class User(
        @SerializedName("color")
        val color: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("isHost")
        val isHost: Boolean,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("role")
        val role: String,
        @SerializedName("team")
        val team: String
    )
}