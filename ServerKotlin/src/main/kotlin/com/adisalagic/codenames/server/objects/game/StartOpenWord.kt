package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event
import com.google.gson.annotations.SerializedName

data class StartOpenWord(
    @SerializedName("word")
    val word: Word
): BaseAPI(Event.GAME_START_OPEN_WORD) {
    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("times")
        val times: MutableList<ULong>

    )
}