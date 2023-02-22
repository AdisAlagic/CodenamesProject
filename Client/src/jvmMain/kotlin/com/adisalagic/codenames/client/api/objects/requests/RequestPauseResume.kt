package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.objects.Event

data class RequestPauseResume(override val user: Host) :
    AdminRequest(Event.REQUEST_PAUSE_RESUME, user)