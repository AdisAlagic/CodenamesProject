package com.adisalagic.codenames.server


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseAPI(
    @SerialName("event")
    val event: String
)