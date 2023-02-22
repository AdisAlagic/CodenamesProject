package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event
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