package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.objects.Event

data class RequestShuffleTeams(override val user: Host)
    : AdminRequest(Event.REQUEST_SHUFFLE_TEAMS, user)
