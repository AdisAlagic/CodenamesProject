package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI


data class RequestJoin(
    val user: User
) : BaseAPI("request_join") {
    data class User(
        val nickname: String
    )
}