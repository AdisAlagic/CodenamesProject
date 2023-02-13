package com.adisalagic.codenames.server.objects.requests

import com.adisalagic.codenames.server.BaseAPI

data class RequestTimer(@Transient val none: Nothing? = null): BaseAPI("request_timer")
