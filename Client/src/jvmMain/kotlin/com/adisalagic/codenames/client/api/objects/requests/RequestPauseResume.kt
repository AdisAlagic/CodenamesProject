package com.adisalagic.codenames.client.api.objects.requests

data class RequestPauseResume(
    @Transient override val user: Host
): AdminRequest("request_pauseresume", user)