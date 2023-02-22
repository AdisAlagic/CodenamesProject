package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event
import com.google.gson.annotations.SerializedName

data class RequestPressWord(
    @SerializedName("user")
    val user: User,
    @SerializedName("word")
    val word: Word
): BaseAPI(Event.REQUEST_PRESS_WORD) {
    data class User(
        @SerializedName("id")
        val id: Int
    )
    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("pressed")
        val pressed: Boolean
    )
}