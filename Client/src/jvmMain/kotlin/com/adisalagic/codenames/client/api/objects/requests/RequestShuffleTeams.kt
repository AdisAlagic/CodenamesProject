package com.adisalagic.codenames.client.api.objects.requests

data class RequestShuffleTeams(@Transient override val user: Host) : AdminRequest("request_shuffleteams", user)
