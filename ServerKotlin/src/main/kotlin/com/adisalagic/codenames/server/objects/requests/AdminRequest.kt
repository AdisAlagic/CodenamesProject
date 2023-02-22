package com.adisalagic.codenames.server.objects.requests

import com.adisalagic.codenames.server.BaseAPI
import com.google.gson.annotations.SerializedName

open class AdminRequest(
    @Transient override val event: Int,
    @Transient @SerializedName("user")
    open val user: Host
): BaseAPI(event) {
    data class Host(
        @SerializedName("id")
        val id: Int,
    )
}