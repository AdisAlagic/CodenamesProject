package com.adisalagic.codenames.server.objects.requests


import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event


data class RequestJoin(
    val user: User
) : BaseAPI(Event.REQUEST_JOIN) {
    data class User(
        val nickname: CharSequence
    )
}