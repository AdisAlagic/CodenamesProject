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
    val turn: Turn,
    @SerializedName("words")
    val words: List<Word>
) : BaseAPI("game_state") {
    data class BlueScore(
        @SerializedName("score")
        val score: Int,
        @SerializedName("team")
        val team: String,
        @SerializedName("logs")
        val logs: List<String>
    )

    data class RedScore(
        @SerializedName("score")
        val score: Int,
        @SerializedName("team")
        val team: String,
        @SerializedName("logs")
        val logs: List<String>
    )

    data class Turn(
        @SerializedName("role")
        val role: String,
        @SerializedName("team")
        val team: String
    )

    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("side")
        val side: String,
        @SerializedName("visible")
        val visible: Boolean,
        @SerializedName("users_pressed")
        val usersPressed: List<PlayerInfo.User>
    )
}