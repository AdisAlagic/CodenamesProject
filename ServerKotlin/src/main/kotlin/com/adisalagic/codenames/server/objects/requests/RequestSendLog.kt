package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import com.google.gson.annotations.SerializedName

data class RequestSendLog(
    @SerializedName("log")
    val log: String,
    @SerializedName("user")
    val user: RequestPressWord.User
): BaseAPI("request_sendlog")