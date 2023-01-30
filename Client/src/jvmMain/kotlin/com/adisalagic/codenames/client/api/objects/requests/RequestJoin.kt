package com.adisalagic.codenames.client.api.objects.requests


import com.adisalagic.codenames.client.api.BaseAPI

data class RequestJoin(
    val user: User
): BaseAPI("request_join") {
    data class User(
        val nickname: String
    )
}