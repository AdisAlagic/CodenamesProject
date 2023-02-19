package com.adisalagic.codenames.client.api.objects.game

import com.adisalagic.codenames.client.api.BaseAPI
import com.google.gson.annotations.SerializedName

data class StartOpenWord(
    @SerializedName("word")
    val word: Word
): BaseAPI("game_start_openword") {
    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("times")
        val times: List<ULong>
    )
}