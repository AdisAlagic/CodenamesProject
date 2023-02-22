package com.adisalagic.codenames.server.objects.requests

import com.adisalagic.codenames.server.BaseAPI
import com.adisalagic.codenames.server.objects.Event

data class RequestTimer(@Transient val none: Nothing? = null): BaseAPI(Event.REQUEST_TIMER)
