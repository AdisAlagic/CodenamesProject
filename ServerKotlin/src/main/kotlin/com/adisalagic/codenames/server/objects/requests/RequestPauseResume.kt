package com.adisalagic.codenames.server.objects.requests

data class RequestPauseResume(override val user: Host) :
    AdminRequest("request_pauseresume", user)