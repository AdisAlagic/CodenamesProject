package com.adisalagic.codenames.server.objects.requests

import com.adisalagic.codenames.server.objects.Event

data class RequestShuffleTeams(override val user: Host)
    : AdminRequest(Event.REQUEST_SHUFFLE_TEAMS, user)
