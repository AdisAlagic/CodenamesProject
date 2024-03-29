package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event
import com.google.gson.annotations.SerializedName

data class GameState(
    @SerializedName("blueScore")
    val blueScore: Score,
    @SerializedName("redScore")
    val redScore: Score,
    @SerializedName("state")
    val state: Int,
    @SerializedName("turn")
    val turn: Turn,
    @SerializedName("words")
    val words: List<Word>,
    @SerializedName("skipWord")
    val skipWord: List<PlayerInfo>
) : BaseAPI(Event.GAME_STATE) {

    data class Score(
        @SerializedName("score")
        val score: Int,
        @SerializedName("team")
        val team: Int,
        @SerializedName("logs")
        val logs: List<String>
    )

    data class Turn(
        @SerializedName("role")
        val role: Int,
        @SerializedName("team")
        val team: Int
    )

    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("side")
        val side: Int,
        @SerializedName("visible")
        val visible: Boolean,
        @SerializedName("users_pressed")
        val usersPressed: List<PlayerInfo.User>
    )
}