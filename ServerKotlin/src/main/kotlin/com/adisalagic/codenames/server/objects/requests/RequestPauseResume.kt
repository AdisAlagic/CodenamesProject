package com.adisalagic.codenames.server.objects.requests

import com.adisalagic.codenames.server.objects.Event

data class RequestPauseResume(override val user: Host) :
    AdminRequest(Event.REQUEST_PAUSE_RESUME, user)