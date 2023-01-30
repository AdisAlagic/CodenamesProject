package com.adisalagic.codenames.server.objects

import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.requests.RequestJoin

class EventConverter(
    private val onRequestJoin: (RequestJoin) -> Unit
) {
    private val REQUEST_JOIN = "request_join"

    fun <T: BaseAPI> provide(obj: T){
        when(obj.event){
            REQUEST_JOIN -> onRequestJoin(obj as RequestJoin)
        }
    }

    fun <T: BaseAPI> isJoinRequest(obj: T): Boolean {
        return obj.event == REQUEST_JOIN
    }
}