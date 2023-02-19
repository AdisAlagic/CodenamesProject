package com.adisalagic.codenames.server.objects.game


import com.adisalagic.codenames.server.BaseAPI
import com.google.gson.annotations.SerializedName

data class StartOpenWord(
    @SerializedName("word")
    val word: Word
): BaseAPI("game_start_openword") {
    data class Word(
        @SerializedName("id")
        val id: Int,
        @SerializedName("times")
        val times: MutableList<ULong>

    )
}