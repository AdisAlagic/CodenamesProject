package com.adisalagic.codenames.client.api.objects.requests

import com.adisalagic.codenames.client.api.BaseAPI
import com.adisalagic.codenames.client.api.objects.Event

data class RequestTimer(@Transient val none: Nothing? = null): BaseAPI(Event.REQUEST_TIMER)
