package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.google.gson.annotations.SerializedName

data class GameState(
    @SerializedName("blueScore")
    val blueScore: BlueScore,
    @SerializedName("redScore")
    val redScore: RedScore,
    @SerializedName("state")
    val state: String,
    @SerializedName("turn")
    val turn: Turn
) : BaseAPI("game_state") {
    data class BlueScore(
        @SerializedName("score")
        val score: Int,
        @SerializedName("team")
        val team: String
    )

    data class RedScore(
        @SerializedName("score")
        val score: Int,
        @SerializedName("team")
        val team: String
    )

    data class Turn(
        @SerializedName("role")
        val role: String,
        @SerializedName("team")
        val team: String
    )
}