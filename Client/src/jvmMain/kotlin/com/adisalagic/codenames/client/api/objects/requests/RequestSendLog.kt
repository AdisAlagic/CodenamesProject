package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.BaseAPI
import com.google.gson.annotations.SerializedName

data class RequestSendLog(
    @SerializedName("log")
    val log: String,
    @SerializedName("user")
    val user: RequestPressWord.User
): BaseAPI("request_sendlog")