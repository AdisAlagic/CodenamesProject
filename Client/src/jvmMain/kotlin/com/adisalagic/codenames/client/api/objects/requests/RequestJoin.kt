package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event

data class RequestJoin(
    val user: User
) : BaseAPI(Event.REQUEST_JOIN) {
    data class User(
        val nickname: CharSequence
    )
}