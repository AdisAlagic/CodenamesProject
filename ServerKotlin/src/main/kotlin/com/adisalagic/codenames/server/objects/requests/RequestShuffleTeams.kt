package com.adisalagic.codenames.server.objects.requests

data class RequestShuffleTeams(override val user: Host) : AdminRequest("request_shuffleteams", user)
